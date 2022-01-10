import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;

import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class Server extends JFrame implements Node{

    private final int port;
    private final JFrame window;

    private final ServerSocket serverSocket;
    private Socket socket;
    private DataInputStream inputStream;
    private DataOutputStream outputStream;

    private final ByteArrayOutputStream inputBuffer;
    private DrawingPanel screen;

    public Server(int port) throws IOException {
        this.port = port;
        this.window = new JFrame();
        this.inputBuffer = new ByteArrayOutputStream(500000);
        this.serverSocket = new ServerSocket(this.port);
        SwingUtilities.invokeLater(new Runnable(){
            public void run(){
                createAndShowGUI();
            }
        });
    }

    @Override
    public void run() throws IOException {
        this.socket = serverSocket.accept();
        this.inputStream = new DataInputStream(new BufferedInputStream(this.socket.getInputStream()));
        this.outputStream = new DataOutputStream(new BufferedOutputStream(this.socket.getOutputStream()));
        this.outputStream.write("<ACCEPTED>".getBytes(StandardCharsets.UTF_8));
        this.outputStream.flush();
        this.inputStream.readNBytes(9);
        while(true){
            this.receiveImage();
            this.screen.setImage(ImageIO.read(new ByteArrayInputStream(this.inputBuffer.toByteArray())));
            this.window.revalidate();
            this.window.repaint();
            this.inputBuffer.reset();
            this.outputStream.write("<CONTINUE>".getBytes(StandardCharsets.UTF_8));
            this.outputStream.flush();
        }
    }

    public void receiveImage() throws IOException {
        int size = this.inputStream.readInt();
        this.outputStream.write("<START>".getBytes(StandardCharsets.UTF_8));
        this.outputStream.flush();
        this.inputBuffer.write(this.inputStream.readNBytes(size));
    }

    private void createAndShowGUI(){
        this.window.setSize(1920,1080);
        this.window.setResizable(true);
        this.window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.window.setLayout(new BorderLayout());
        this.window.setLocationRelativeTo(null);

        try {
            BufferedImage image = null;
            this.screen = new DrawingPanel(image);
            this.window.add(this.screen);
            this.window.pack();
            this.window.setVisible(true);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public JFrame getWindow() {
        return window;
    }

    class DrawingPanel extends JPanel{
        private BufferedImage image;

        public DrawingPanel(BufferedImage image){
            this.image = image;
            setBorder(BorderFactory.createLineBorder(Color.black));
        }

        public Dimension getPreferredSize(){
            return new Dimension(1920,1080);
        }

        public void paintComponent(Graphics g){
            super.paintComponent(g);
            g.drawString("Das hier  ist mein erster Text!", 10,20);
            g.setColor(Color.RED);
            g.drawImage(this.image,0,0, getWindow().getWidth() , getWindow().getHeight(),null);
        }

        public void setImage(BufferedImage image) {
            this.image = image;
        }
    }
}


