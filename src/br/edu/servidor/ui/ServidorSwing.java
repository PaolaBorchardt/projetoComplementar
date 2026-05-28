package br.edu.servidor.ui;

import br.edu.servidor.network.TCPServidor;
import br.edu.servidor.network.UDPServidor;
import br.edu.servidor.service.GerenciadorArquivos;
import java.awt.*;
import javax.swing.*;

public class ServidorSwing extends JFrame {

    private JTable tabelaArquivos;
    private JTextArea areaLogs;
    private JButton btnAtualizar;
    private JButton btnIniciarServidor;
    private JLabel lblStatus;
    private JRadioButton radioTCP;
    private JRadioButton radioUDP;
    private JRadioButton radioAmbos;
    private TabelaArquivosModel tabelaModel;
    private GerenciadorArquivos gerenciador;
    private TCPServidor tcpServidor;
    private UDPServidor udpServidor;
    private static final int PORTA_TCP = 5000;
    private static final int PORTA_UDP = 6000;
    private boolean servidorIniciado = false;

    public ServidorSwing() {
        setTitle("Servidor de Hospedagem de Arquivos");
        setSize(900, 550);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        gerenciador = new GerenciadorArquivos("arquivos");
        inicializarComponentes();
        carregarArquivos();
    }

    private void inicializarComponentes() {
        tabelaModel = new TabelaArquivosModel();
        tabelaArquivos = new JTable(tabelaModel);
        areaLogs = new JTextArea();
        areaLogs.setEditable(false);
        btnAtualizar = new JButton("Atualizar arquivos");
        btnIniciarServidor = new JButton("Iniciar servidor");
        lblStatus = new JLabel("Servidor ainda não iniciado");
        radioTCP = new JRadioButton("TCP");
        radioUDP = new JRadioButton("UDP");
        radioAmbos = new JRadioButton("TCP + UDP", true);
        ButtonGroup grupoProtocolos = new ButtonGroup();
        grupoProtocolos.add(radioTCP);
        grupoProtocolos.add(radioUDP);
        grupoProtocolos.add(radioAmbos);
        JPanel painelTopo = new JPanel(new FlowLayout(FlowLayout.LEFT));
        painelTopo.add(lblStatus);
        painelTopo.add(btnAtualizar);
        painelTopo.add(new JLabel("Modo:"));
        painelTopo.add(radioTCP);
        painelTopo.add(radioUDP);
        painelTopo.add(radioAmbos);
        painelTopo.add(btnIniciarServidor);
        JPanel painelTabela = new JPanel(new BorderLayout());
        painelTabela.setBorder(BorderFactory.createTitledBorder("Arquivos hospedados"));
        painelTabela.add(new JScrollPane(tabelaArquivos), BorderLayout.CENTER);
        JPanel painelLogs = new JPanel(new BorderLayout());
        painelLogs.setBorder(BorderFactory.createTitledBorder("Logs do servidor"));
        painelLogs.add(new JScrollPane(areaLogs), BorderLayout.CENTER);
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, painelTabela, painelLogs);
        splitPane.setDividerLocation(280);
        add(painelTopo, BorderLayout.NORTH);
        add(splitPane, BorderLayout.CENTER);
        btnAtualizar.addActionListener(e -> carregarArquivos());
        btnIniciarServidor.addActionListener(e -> iniciarServidores());
    }

    private void carregarArquivos() {
        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() {
                tabelaModel.setArquivos(gerenciador.listarArquivos());
                return null;
            }

            @Override
            protected void done() {
                lblStatus.setText("Arquivos atualizados");
                adicionarLog("Tabela de arquivos atualizada.");
            }
        };
        worker.execute();
    }

    private void iniciarServidores() {
        if (servidorIniciado) {
            JOptionPane.showMessageDialog(this,
                    "O servidor já foi iniciado. Para mudar o modo, feche e abra novamente.");
            return;
        }
        if (radioTCP.isSelected()) {
            iniciarTCP();
            lblStatus.setText("Servidor iniciado somente em TCP");
        } else if (radioUDP.isSelected()) {
            iniciarUDP();
            lblStatus.setText("Servidor iniciado somente em UDP");
        } else {
            iniciarTCP();
            iniciarUDP();
            lblStatus.setText("Servidor iniciado em TCP + UDP");
        }
        servidorIniciado = true;
        radioTCP.setEnabled(false);
        radioUDP.setEnabled(false);
        radioAmbos.setEnabled(false);
        btnIniciarServidor.setEnabled(false);
    }

    private void iniciarTCP() {
        tcpServidor = new TCPServidor(PORTA_TCP, gerenciador, this::adicionarLog, this::carregarArquivos);
        SwingWorker<Void, Void> workerTCP = new SwingWorker<>() {
            @Override
            protected Void doInBackground() {
                tcpServidor.iniciar();
                return null;
            }
        };
        workerTCP.execute();
        adicionarLog("Servidor TCP iniciado na porta " + PORTA_TCP);
    }

    private void iniciarUDP() {
        udpServidor = new UDPServidor(PORTA_UDP, gerenciador, this::adicionarLog);
        SwingWorker<Void, Void> workerUDP = new SwingWorker<>() {
            @Override
            protected Void doInBackground() {
                udpServidor.iniciar();
                return null;
            }
        };
        workerUDP.execute();
        adicionarLog("Servidor UDP iniciado na porta " + PORTA_UDP);
    }

    private void adicionarLog(String mensagem) {
        SwingUtilities.invokeLater(() -> {
            areaLogs.append(mensagem + "\n");
        });
    }
}
