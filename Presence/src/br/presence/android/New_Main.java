package br.presence.android;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;


//import br.presence.R;
//import jim.h.common.android.zxingjar.demo.R;
import jim.h.common.android.zxinglib.integrator.IntentIntegrator;
import jim.h.common.android.zxinglib.integrator.IntentResult;
import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.graphics.Typeface;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;
import au.com.bytecode.opencsv.CSVWriter;

@SuppressLint("NewApi")
public class New_Main extends Activity {

	String resultado_scan, nome_evento;

	@SuppressLint("NewApi")
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setUpActionBar();
		
		if (savedInstanceState == null) {
			checkGPS();
		}

		getNomeEvento();
		changeFonts();
	}

	private void setUpActionBar() {
		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB) {
			setContentView(R.layout.new_main);
			ActionBar actionBar = getActionBar();
			actionBar.setDisplayHomeAsUpEnabled(true);

		} else {
			setContentView(R.layout.new_main_2x);
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		default:
			about();
			break;
		}
		return true;
	}

	public void about() {
		Intent intent = new Intent(this, About.class);
		startActivity(intent);
	}

	public void getNomeEvento() {
		Intent intent = getIntent();
		Bundle bundle = intent.getExtras();
		nome_evento = bundle.getString("NOME_EVENTO");
	}

	public void changeFonts() {
		Button ler_qr = (Button) findViewById(R.id.scan_button);
		Button list_cad = (Button) findViewById(R.id.botao_listarCadastradosEvento);
		Button envia_arq = (Button) findViewById(R.id.botao_enviarArquivo);

		Typeface tf = Typeface.createFromAsset(getAssets(),
				"fonts/comfortaa.ttf");
		ler_qr.setTypeface(tf);
		list_cad.setTypeface(tf);
		envia_arq.setTypeface(tf);
	}

	public void lerQR(View view) {

		try {
			IntentIntegrator.initiateScan(this, R.layout.capture,
					R.id.viewfinder_view, R.id.preview_view, true);
		} catch (Exception e) {
			Toast.makeText(getApplicationContext(),
					"Não foi possível iniciar a câmera", Toast.LENGTH_LONG)
					.show();
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		switch (requestCode) {
		case IntentIntegrator.REQUEST_CODE:
			IntentResult scanResult = IntentIntegrator.parseActivityResult(
					requestCode, resultCode, data);
			if (scanResult == null) {
				return;
			}
			final String result = scanResult.getContents();
			this.resultado_scan = result;

			if (result != null) {

				new Thread(new Runnable() {

					public void run() {
						Intent tela = new Intent(getBaseContext(),
								New_Resultado.class);
						Bundle parametros = new Bundle();
						parametros.putString("RESULTADO", result);
						parametros.putString("NOME_EVENTO", nome_evento);
						tela.putExtras(parametros);
						startActivity(tela);
					}

				}).start();

			}

			break;
		}
	}

	public void enviarArquivo(View view) {

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("ATENÇÃO");
		builder.setMessage("Você gostaria de manter os dados do evento ou de apagá-los?");
		builder.setIcon(android.R.drawable.ic_dialog_alert);
		builder.setPositiveButton("Manter",
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						mandarArquivo(1);
					}
				});

		builder.setNegativeButton("Apagar",
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						mandarArquivo(0);
					}
				});

		builder.show();

	}

	public void mandarArquivo(int manter) {
		DatabaseAdapter dba = new DatabaseAdapter(getBaseContext());

		File exportDir = new File(Environment.getExternalStorageDirectory()
				.getPath(), "");

		if (!exportDir.exists()) {
			exportDir.mkdir();
		} else {
			File file = new File(exportDir, "Lista_Presentes_" + nome_evento + ".csv");
			try {
				file.createNewFile();
				CSVWriter csvWrite = new CSVWriter(new FileWriter(file), ';');
				dba.open();
				
				Cursor curCSV = dba.getAllUsers(nome_evento);

				while (curCSV.moveToNext()) {
					String novoArray[] = curCSV.getString(0).split(";");
					csvWrite.writeNext(novoArray);
				}
				csvWrite.close();
				curCSV.close();
				dba.close();
				Toast.makeText(getBaseContext(),
						"Arquivo foi criado com sucesso.", Toast.LENGTH_SHORT)
						.show();
			} catch (SQLException e) {
				Log.e("Script", e.getMessage());
			} catch (IOException e) {
				Log.e("Script", e.getMessage());
			}
		}
		chooseAppEmail(manter);
	}

	public void chooseAppEmail(int manter) {
		DatabaseAdapter dba = new DatabaseAdapter(getBaseContext());
		dba.open();
		Cursor infor = dba.getUniqueEvent(nome_evento);
		String info = "Informações técnicas do evento. \n";
		while(infor.moveToNext()){
			info += "Nome do evento: " + infor.getString(0) +"\n";
			info += "Responsável pelo evento: " + infor.getString(1) +"\n";
			info += "Onde aconteceu: " + infor.getString(2) +"\n";
			info += "Quando aconteceu: " + infor.getString(3) +"\n";
		}
		if(manter == 0){
			dba.deleteEvent();
		}
		dba.close();
		Intent intent = new Intent(Intent.ACTION_SEND);
		intent.setType("text/plain");
		intent.putExtra(Intent.EXTRA_SUBJECT, "Lista de Presença");
		intent.putExtra(Intent.EXTRA_TEXT, info);
		File root = Environment.getExternalStorageDirectory();
		File file = new File(root, "Lista_Presentes_" + nome_evento + ".csv");
		if (!file.exists() || !file.canRead()) {
			Toast.makeText(this, "Arquivo inexistente ou não pode ser lido.",
					Toast.LENGTH_SHORT).show();
			return;
		}
		Uri uri = Uri.parse("file://" + file);
		intent.putExtra(Intent.EXTRA_STREAM, uri);
		startActivity(Intent.createChooser(intent, "Enviar arquivo via"));
	}

	public void listarCadastrados(View view) {
		String n_evento = nome_evento;
		Bundle bundle = new Bundle();
		bundle.putString("N_EVENTO", n_evento);
		Intent intent = new Intent(this, Cadastrados.class);
		intent.putExtras(bundle);
		startActivity(intent);
	}

	private void checkGPS() {
		LocationManager lManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		boolean enabled = lManager
				.isProviderEnabled(LocationManager.GPS_PROVIDER);

		if (enabled == false) {
			new AlertDialog.Builder(this)
					.setTitle("GPS desligado")
					.setMessage(
							"É aconselhável usar o GPS. Gostaria de ativá-lo?")
					.setPositiveButton(android.R.string.yes,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									startActivity(new Intent(
											android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
								}
							})
					.setNegativeButton(android.R.string.no,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									dialog.cancel();
								}
							}).setIcon(android.R.drawable.ic_dialog_alert)
					.show();
		}

	}

}
