package br.edu.servidor;

import br.edu.servidor.ui.ServidorSwing;

import javax.swing.SwingUtilities;

public class ServidorApp {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            ServidorSwing tela = new ServidorSwing();
            tela.setVisible(true);
        });
    }
}
