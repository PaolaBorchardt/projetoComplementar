package br.edu.servidor.network;

import br.edu.servidor.service.GerenciadorArquivos;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.nio.charset.StandardCharsets;
import java.util.function.Consumer;

public class UDPServidor {

    private final int porta;
    private final GerenciadorArquivos gerenciador;
    private final Consumer<String> log;
    private boolean executando = false;

    public UDPServidor(int porta, GerenciadorArquivos gerenciador, Consumer<String> log) {
        this.porta = porta;
        this.gerenciador = gerenciador;
        this.log = log;
    }

    public void iniciar() {
        executando = true;
        try (DatagramSocket socket = new DatagramSocket(porta)) {
            log.accept("Servidor UDP iniciado na porta " + porta);
            byte[] buffer = new byte[4096];
            while (executando) {
                DatagramPacket pacoteRecebido = new DatagramPacket(buffer, buffer.length);
                socket.receive(pacoteRecebido);
                String mensagem = new String(pacoteRecebido.getData(), 0, pacoteRecebido.getLength(), StandardCharsets.UTF_8);
                String resposta;
                if (mensagem.equalsIgnoreCase("DISCOVER_SERVER")) {
                    resposta = "SERVIDOR_ARQUIVOS_ATIVO";
                } else if (mensagem.equalsIgnoreCase("LIST")) {
                    resposta = gerenciador.gerarListaTexto();
                } else {
                    resposta = "ERRO Comando UDP inválido.";
                }
                byte[] dadosResposta = resposta.getBytes(StandardCharsets.UTF_8);
                DatagramPacket pacoteResposta = new DatagramPacket(dadosResposta, dadosResposta.length,
                        pacoteRecebido.getAddress(), pacoteRecebido.getPort());
                socket.send(pacoteResposta);
                log.accept("Mensagem UDP recebida: " + mensagem);
            }
        } catch (Exception e) {
            log.accept("Erro no servidor UDP: " + e.getMessage());
        }
    }

    public void parar() {
        executando = false;
    }
}
