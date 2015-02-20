package br.presence.android;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


//import br.presence.R;
//import jim.h.common.android.zxingjar.demo.R;
import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

@SuppressLint("NewApi")
public class MainActivity extends Activity {
	@SuppressLint("NewApi")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setUpActionBar();
		lista_eventos();

	}

	private void setUpActionBar() {
		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB) {
			setContentView(R.layout.main);
			ActionBar actionBar = getActionBar();
			actionBar.setDisplayHomeAsUpEnabled(true);
		} else {
			setContentView(R.layout.main_2x);
		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main_activity_action, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.add_event:
			cadastrarEvento();
			break;
		
		case R.id.show_about:
			about();
			break;
		default:
			break;
		}
		return true;
	}

	public void about() {
		Intent intent = new Intent(this, About.class);
		startActivity(intent);
	}

	public void lista_eventos() {
		final ListView listview = (ListView) findViewById(android.R.id.list);

		final ArrayList<String> nomes = new ArrayList<String>();

		DatabaseAdapter dba = new DatabaseAdapter(getBaseContext());
		dba.open();
		Cursor cursor = dba.getAllEvents();

		while (cursor.moveToNext()) {
			nomes.add(cursor.getString(cursor
					.getColumnIndex(DatabaseAdapter.CAMPO_NOME)));
		}

		final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, nomes);
		listview.setAdapter(adapter);

		listview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				String str = listview.getItemAtPosition(arg2).toString();
				Bundle parametros = new Bundle();
				parametros.putString("NOME_EVENTO", str);
				Intent intent = new Intent(getBaseContext(), New_Main.class);
				intent.putExtras(parametros);
				startActivity(intent);
			}

		});
	}

	public void cadastrarEvento() {
		Intent intent = new Intent(this, CadastroEvento.class);
		startActivity(intent);
		finish();
	}
}
