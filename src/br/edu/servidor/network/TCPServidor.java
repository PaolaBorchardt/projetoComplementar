package br.edu.servidor.network;

import br.edu.servidor.service.GerenciadorArquivos;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.function.Consumer;
import javax.swing.SwingUtilities;

public class TCPServidor {

    private final int porta;
    private final GerenciadorArquivos gerenciador;
    private final Consumer<String> log;
    private final Runnable aoAtualizarTabela;
    private boolean executando = false;

    public TCPServidor(int porta, GerenciadorArquivos gerenciador, Consumer<String> log, Runnable aoAtualizarTabela) {
        this.porta = porta;
        this.gerenciador = gerenciador;
        this.log = log;
        this.aoAtualizarTabela = aoAtualizarTabela;
    }

    public void iniciar() {
        executando = true;
        try (ServerSocket serverSocket = new ServerSocket(porta)) {
            log.accept("Servidor TCP iniciado na porta " + porta);
            while (executando) {
                Socket socket = serverSocket.accept();
                new Thread(() -> atenderCliente(socket)).start();
            }
        } catch (IOException e) {
            log.accept("Erro no servidor TCP: " + e.getMessage());
        }
    }

    private void atenderCliente(Socket socket) {
        try (DataInputStream entrada = new DataInputStream(socket.getInputStream()); DataOutputStream saida = new DataOutputStream(socket.getOutputStream())) {
            String comando = entrada.readUTF();
            if (comando.equalsIgnoreCase("LIST")) {
                enviarLista(saida);
            } else if (comando.startsWith("DOWNLOAD")) {
                String nomeArquivo = comando.replace("DOWNLOAD", "").trim();
                enviarArquivo(nomeArquivo, saida);
            } else {
                saida.writeUTF("ERRO Comando inválido");
            }
        } catch (IOException e) {
            log.accept("Erro ao atender cliente: " + e.getMessage());
        }
    }

    private void enviarLista(DataOutputStream saida) throws IOException {
        String lista = gerenciador.gerarListaTexto();
        saida.writeUTF("OK");
        saida.writeUTF(lista);
        log.accept("Lista de arquivos enviada para cliente TCP.");
    }

    private void enviarArquivo(String nomeArquivo, DataOutputStream saida) throws IOException {
        File arquivo = gerenciador.buscarArquivo(nomeArquivo);
        if (arquivo == null) {
            saida.writeUTF("ERRO");
            saida.writeUTF("Arquivo não encontrado.");
            return;
        }
        saida.writeUTF("OK");
        saida.writeUTF(arquivo.getName());
        saida.writeLong(arquivo.length());
        try (FileInputStream fis = new FileInputStream(arquivo)) {
            byte[] buffer = new byte[4096];
            int bytesLidos;
            while ((bytesLidos = fis.read(buffer)) != -1) {
                saida.write(buffer, 0, bytesLidos);
            }
        }
        gerenciador.incrementarDownload(nomeArquivo);
        SwingUtilities.invokeLater(aoAtualizarTabela);
        log.accept("Arquivo baixado via TCP: " + nomeArquivo);
    }

    public void parar() {
        executando = false;
    }
}
