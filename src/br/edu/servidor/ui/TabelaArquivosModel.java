package br.edu.servidor.ui;

import br.edu.servidor.model.ArquivoInfo;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;

public class TabelaArquivosModel extends AbstractTableModel {

    private final String[] colunas = {"Arquivo", "Tamanho", "Downloads"};
    private List<ArquivoInfo> arquivos = new ArrayList<>();

    public void setArquivos(List<ArquivoInfo> arquivos) {
        this.arquivos = arquivos;
        fireTableDataChanged();
    }

    @Override
    public int getRowCount() {
        return arquivos.size();
    }

    @Override
    public int getColumnCount() {
        return colunas.length;
    }

    @Override
    public String getColumnName(int column) {
        return colunas[column];
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        ArquivoInfo arquivo = arquivos.get(rowIndex);
        return switch (columnIndex) {
            case 0 ->
                arquivo.getNome();
            case 1 ->
                arquivo.getTamanhoFormatado();
            case 2 ->
                arquivo.getDownloads();
            default ->
                "";
        };
    }
}
