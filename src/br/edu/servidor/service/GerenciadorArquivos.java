package br.edu.servidor.service;

import br.edu.servidor.model.ArquivoInfo;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class GerenciadorArquivos {

    private final File diretorio;
    private final ConcurrentHashMap<String, Integer> contadorDownloads;

    public GerenciadorArquivos(String caminhoDiretorio) {
        this.diretorio = new File(caminhoDiretorio);
        this.contadorDownloads = new ConcurrentHashMap<>();
        if (!diretorio.exists()) {
            diretorio.mkdirs();
        }
    }

    public synchronized List<ArquivoInfo> listarArquivos() {
        List<ArquivoInfo> lista = new ArrayList<>();
        File[] arquivos = diretorio.listFiles();

        if (arquivos != null) {
            for (File arquivo : arquivos) {
                if (arquivo.isFile()) {
                    int downloads = contadorDownloads.getOrDefault(
                            arquivo.getName(), 0);

                    lista.add(
                            new ArquivoInfo(
                                    arquivo.getName(),
                                    arquivo.length(),
                                    downloads));
                }
            }
        }

        // ADIÇÃO
        // Ordena os arquivos pelo tamanho em ordem crescente
        selectionSort(lista);

        return lista;
    }

    public synchronized File buscarArquivo(String nomeArquivo) {
        File arquivo = new File(diretorio, nomeArquivo);
        if (arquivo.exists() && arquivo.isFile()) {
            return arquivo;
        }
        return null;
    }

    public synchronized void incrementarDownload(String nomeArquivo) {
        contadorDownloads.put(nomeArquivo, contadorDownloads.getOrDefault(nomeArquivo, 0) + 1);
    }

    public String gerarListaTexto() {
        StringBuilder sb = new StringBuilder();
        for (ArquivoInfo arquivo : listarArquivos()) {
            sb.append(arquivo.getNome()).append(";").append(arquivo.getTamanho()).append(";")
                    .append(arquivo.getDownloads()).append("\n");
        }
        return sb.toString();
    }

    // ADIÇÃO
    // Método Selection Sort para ordenar pelo tamanho do arquivo
    private void selectionSort(List<ArquivoInfo> lista) {

        int n = lista.size();

        for (int i = 0; i < n - 1; i++) {

            int menor = i;

            for (int j = i + 1; j < n; j++) {

                if (lista.get(j).getTamanho()
                        < lista.get(menor).getTamanho()) {

                    menor = j;
                }
            }

            ArquivoInfo temp = lista.get(i);
            lista.set(i, lista.get(menor));
            lista.set(menor, temp);
        }
    }
}