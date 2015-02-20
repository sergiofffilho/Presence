package br.presence.android;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


//import br.presence.R;
//import jim.h.common.android.zxingjar.demo.R;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class Cadastrados extends ListActivity {

	private String nome;
	private List<String> nomes = new ArrayList<String>();

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.cadastrados);

		getNomeEvento();

		getNomesCadastrados();

		fillListView();

	}

	public void getNomeEvento() {
		Intent intent = getIntent();
		Bundle bundle = intent.getExtras();
		nome = bundle.getString("N_EVENTO");
	}

	public void getNomesCadastrados() {
		DatabaseAdapter dba = new DatabaseAdapter(getBaseContext());
		dba.open();
		Cursor cursor = dba.getAllUsers(nome);

		while (cursor.moveToNext()) {
			nomes.add(cursor.getString(cursor.getColumnIndex(DatabaseAdapter.CAMPO_TEXTO)));
		}
	}
	
	public void fillListView() {
		ListView listview = (ListView) findViewById(android.R.id.list);
		final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, nomes);
		listview.setAdapter(adapter);
	}

}
