package br.presence.android;

import java.util.Calendar;


//import br.presence.R;
//import jim.h.common.android.zxingjar.demo.R;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

public class CadastroEvento extends Activity {

	private EditText nome_evento;
	private EditText local_evento;
	private EditText responsavel_evento;
	private EditText data_evento;
	static final int DATE_DIALOG_ID = 0;

	private int mYear;
	private int mMonth;
	private int mDay;

	@SuppressWarnings("deprecation")
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.cadastro_evento);

		getFields();
		changeFont();

		final Calendar c = Calendar.getInstance();
		mYear = c.get(Calendar.YEAR);
		mMonth = c.get(Calendar.MONTH);
		mDay = c.get(Calendar.DAY_OF_MONTH);

		data_evento.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				showDialog(DATE_DIALOG_ID);

			}
		});
	}

	public void getFields() {

		nome_evento = (EditText) findViewById(R.id.nome_evento);
		local_evento = (EditText) findViewById(R.id.local_evento);
		responsavel_evento = (EditText) findViewById(R.id.responsavel_evento);
		data_evento = (EditText) findViewById(R.id.data_evento);
	}

	public void changeFont() {
		Typeface tf = Typeface.createFromAsset(getAssets(),
				"fonts/comfortaa.ttf");
		nome_evento.setTypeface(tf);
		local_evento.setTypeface(tf);
		responsavel_evento.setTypeface(tf);
		data_evento.setTypeface(tf);
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case DATE_DIALOG_ID:
			return new DatePickerDialog(this, mDateSetListener, mYear, mMonth,
					mDay);
		}
		return null;
	}

	// the callback received when the user "sets" the date in the dialog
	private DatePickerDialog.OnDateSetListener mDateSetListener = new DatePickerDialog.OnDateSetListener() {
		public void onDateSet(DatePicker view, int year, int monthOfYear,
				int dayOfMonth) {
			mYear = year;
			mMonth = monthOfYear;
			mDay = dayOfMonth;
			updateDisplay();
		}
	};

	private void updateDisplay() {
		data_evento.setText(new StringBuilder()
				// Month is 0 based so add 1
				.append(mDay).append(" de ").append(mes()).append(" de ")
				.append(mYear));
	}

	private String mes() {
		if (mMonth + 1 == 1) {
			return "Janeiro";
		} else if (mMonth + 1 == 2) {
			return "Fevereiro";
		} else if (mMonth + 1 == 3) {
			return "Mar�o";
		} else if (mMonth + 1 == 4) {
			return "Abril";
		} else if (mMonth + 1 == 5) {
			return "Maio";
		} else if (mMonth + 1 == 6) {
			return "Junho";
		} else if (mMonth + 1 == 7) {
			return "Julho";
		} else if (mMonth + 1 == 8) {
			return "Agosto";
		} else if (mMonth + 1 == 9) {
			return "Setembro";
		} else if (mMonth + 1 == 10) {
			return "Outubro";
		} else if (mMonth + 1 == 11) {
			return "Novembro";
		} else if (mMonth + 1 == 12) {
			return "Dezembro";
		} else {
			return null;
		}
	}

	public void cadastrar(View view) {

		if (nome_evento.getText().toString().equals("")) {
			Toast.makeText(getApplicationContext(),
					"O campo Nome está em branco", Toast.LENGTH_LONG).show();
		} else if (local_evento.getText().toString().equals("")) {
			Toast.makeText(getApplicationContext(),
					"O campo Local está em branco", Toast.LENGTH_LONG).show();
		} else if (responsavel_evento.getText().toString().equals("")) {
			Toast.makeText(getApplicationContext(),
					"O campo Responsável está em branco", Toast.LENGTH_LONG)
					.show();
		} else if (data_evento.getText().toString().equals("")) {
			Toast.makeText(getApplicationContext(),
					"O campo Data está em branco", Toast.LENGTH_LONG).show();
		}

		if (!nome_evento.getText().toString().equals("")
				&& !local_evento.getText().toString().equals("")
				&& !responsavel_evento.getText().toString().equals("")
				&& !data_evento.getText().toString().equals("")) {
			DatabaseAdapter dba = new DatabaseAdapter(getApplicationContext());
			dba.open();
			dba.insertEvent(nome_evento.getText().toString(), local_evento
					.getText().toString(), responsavel_evento.getText()
					.toString(), data_evento.getText().toString());
			Toast.makeText(getBaseContext(), "Evento cadastrado com sucesso!",
					Toast.LENGTH_SHORT).show();
			dba.close();
			showScreen();
			finish();
		}

	}

	public void showScreen() {
		Intent intent = new Intent(this, MainActivity.class);
		startActivity(intent);
	}
}
