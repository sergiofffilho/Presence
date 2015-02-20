package br.presence.android;

import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;
import java.util.Locale;


//import br.presence.R;
//import jim.h.common.android.zxingjar.demo.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class New_Resultado extends Activity {

	private Button novoCodigo;
	private String hora, evento;
	private DatabaseAdapter db;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.new_resultado);
		getInfo();
	}

	public void getInfo() {
		Intent intent = getIntent();
		if (intent != null && intent.getExtras() != null) {
			Bundle bundle = intent.getExtras();
			evento = bundle.getString("NOME_EVENTO");
			getData(bundle.getString("RESULTADO").toString());
		}
	}

	public void getData(String resultado) {
		novoCodigo = (Button) findViewById(R.id.et_new_resultado);
		getTime();
		novoCodigo.setText(resultado + hora);
	}

	public void getTime() {
		Locale locale = new Locale("pt", "BR");
		GregorianCalendar calendar = new GregorianCalendar();
		SimpleDateFormat formatador = new SimpleDateFormat(
				"dd' de 'MMMMM' de 'yyyy';'HH':'mm':'ss';'", locale);

		hora = formatador.format(calendar.getTime()).toString();
	}

	public void cadastrar(View view) {
		if (!novoCodigo.getText().toString().equals("")) {
			db = new DatabaseAdapter(getBaseContext());
			db.open();
			db.insertDados(novoCodigo.getText().toString(), evento.toString());
			db.close();
			Toast.makeText(this, "Cadastrado com sucesso!",Toast.LENGTH_LONG).show();
		} else {
			Toast.makeText(this,
					"O campo está em branco. Leia um QRCode válido.",
					Toast.LENGTH_LONG).show();
		}
		finish();
	}
	
	public void naoEditavel(View view){
		Toast.makeText(getApplicationContext(), "Não é possível editar o conteúdo.", Toast.LENGTH_LONG).show();
	}
}
