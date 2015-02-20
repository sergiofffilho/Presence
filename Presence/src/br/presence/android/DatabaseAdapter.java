package br.presence.android;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseAdapter {
	public static final String TABELA = "participantes";
	public static final String CAMPO_ID = "_id";
	public static final String CAMPO_NOME_EVENTO = "nome_evento";
	public static final String CAMPO_TEXTO = "texto";
	
	public static final String TABELA_EVENTO = "evento";
	public static final String CAMPO_NOME = "nome";
	public static final String CAMPO_LOCAL_EVENTO = "local";
	public static final String CAMPO_RESPONSAVEL_EVENTO = "responsavel";
	public static final String CAMPO_DATA_EVENTO = "data";

	public static final String NOME_BD = "bdpacce";
	public static final int VERSAO_DB = 1;

	private final Context context;
	private DatabaseHelper helper;
	private static SQLiteDatabase db;

	public DatabaseAdapter(Context context) {
		this.context = context;
		helper = new DatabaseHelper(context);
	}

	public static class DatabaseHelper extends SQLiteOpenHelper {
		public DatabaseHelper(Context context) {
			super(context, NOME_BD, null, VERSAO_DB);
		}

		@Override
		public void onCreate(SQLiteDatabase arg0) {
			createTable(arg0);
		}
		
		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			Log.w("DatabaseAdapter", "Upgrading database from version "
					+ oldVersion + " to version " + newVersion);
			db.execSQL("DROP TABLE IF EXISTS " + TABELA);
			createTable(db);
		}

		private void createTable(SQLiteDatabase db) {
			String str = "CREATE TABLE " +TABELA+ "(" + CAMPO_ID 
					+ " INTEGER PRIMARY KEY AUTOINCREMENT, "
					+ CAMPO_TEXTO + " TEXT NOT NULL,"
					+ CAMPO_NOME_EVENTO + " TEXT NOT NULL);";
			
			String str_evento = "CREATE TABLE " + TABELA_EVENTO + " (" 
					+ CAMPO_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " 
					+ CAMPO_NOME + " TEXT NOT NULL," 
					+ CAMPO_LOCAL_EVENTO + " TEXT NOT NULL,"
					+ CAMPO_RESPONSAVEL_EVENTO + " TEXT NOT NULL, data TEXT NOT NULL);";
			try {
				db.execSQL(str);
				db.execSQL(str_evento);
			} catch (SQLException e) {
				e.printStackTrace();
			}

		}
	}

	public DatabaseAdapter open() throws SQLException {
		db = helper.getWritableDatabase();
		return this;
	}

	public void close() {
		helper.close();
		db.close();
	}

	public long insertDados(String texto, String nome_evento) {
		ContentValues values = new ContentValues();
		values.put("texto", texto);
		values.put("nome_evento", nome_evento);
		return db.insert(TABELA, null, values);
	}

	
	public long insertEvent(String nome, String local, String responsavel,
			String data) {
		ContentValues values = new ContentValues();
		values.put(CAMPO_NOME, nome);
		values.put(CAMPO_LOCAL_EVENTO, local);
		values.put(CAMPO_RESPONSAVEL_EVENTO, responsavel);
		values.put(CAMPO_DATA_EVENTO, data);
		;
		return db.insert(TABELA_EVENTO, null, values);
	}

	public Cursor getAllUsers(String evento_nome) {
		return db.rawQuery("SELECT texto FROM participantes WHERE nome_evento = '"
				+ evento_nome + "'" + " ORDER BY texto", null);
	}

	public Cursor getAllEvents() {
		return db.query(TABELA_EVENTO, new String[] { CAMPO_ID, CAMPO_NOME }, null, null,
				null, null, CAMPO_ID);
	}

	public Cursor getUniqueEvent(String nome_evento) {
		return db.query(TABELA_EVENTO, new String[] { CAMPO_NOME, CAMPO_RESPONSAVEL_EVENTO, CAMPO_LOCAL_EVENTO, CAMPO_DATA_EVENTO}, " nome=?", new String[] { nome_evento }, null,null, null);
	}

	public int deleteEvent() {
		return db.delete(TABELA_EVENTO, null, null);
	}

}

