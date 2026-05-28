package br.edu.servidor.model;

public class ArquivoInfo {

    private String nome;
    private long tamanho;
    private int downloads;

    public ArquivoInfo(String nome, long tamanho, int downloads) {
        this.nome = nome;
        this.tamanho = tamanho;
        this.downloads = downloads;
    }

    public String getNome() {
        return nome;
    }

    public long getTamanho() {
        return tamanho;
    }

    public int getDownloads() {
        return downloads;
    }

    public void incrementarDownloads() {
        downloads++;
    }

    public String getTamanhoFormatado() {
        if (tamanho < 1024) {
            return tamanho + " B";
        } else if (tamanho < 1024 * 1024) {
            return String.format("%.2f KB", tamanho / 1024.0);
        } else {
            return String.format("%.2f MB", tamanho / (1024.0 * 1024.0));
        }
    }
}
