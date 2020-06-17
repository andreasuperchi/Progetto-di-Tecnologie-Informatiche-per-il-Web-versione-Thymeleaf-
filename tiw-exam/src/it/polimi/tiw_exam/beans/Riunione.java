package it.polimi.tiw_exam.beans;

public class Riunione {
	private int id;
	private String titolo;
	private String data;
	private String ora;
	private String durata;
	private int num_max_partecipanti;
	private int host;
	
	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public String getTitolo() {
		return titolo;
	}
	
	public void setTitolo(String titolo) {
		this.titolo = titolo;
	}
	
	public String getData() {
		return data;
	}
	
	public void setData(String data) {
		this.data = data;
	}
	
	public String getOra() {
		return ora;
	}
	
	public void setOra(String ora) {
		this.ora = ora;
	}
	
	public String getDurata() {
		return durata;
	}
	
	public void setDurata(String durata) {
		this.durata = durata;
	}
	
	public int getNum_max_partecipanti() {
		return num_max_partecipanti;
	}
	
	public void setNum_max_partecipanti(int num_max_partecipanti) {
		this.num_max_partecipanti = num_max_partecipanti;
	}
	
	public int getHost() {
		return host;
	}
	
	public void setHost(int host) {
		this.host = host;
	}
}
