import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class Client implements Node{

    private final InetAddress address;
    private final int port;
    private int fileSize;

    private DataInputStream inputStream;
    private DataOutputStream outputStream;
    private final Socket socket;

    private final Robot screenshotTaker;
    private final Rectangle captureArea;
    private final BufferedImage screenshot;
    private ByteArrayOutputStream outputImageBytes;
    private ImageOutputStream imageOutputStream;



    public Client(String address, int port) throws IOException, AWTException {
        this.address = InetAddress.getByName(address);
        this.port = port;
        this.screenshotTaker = new Robot();
        this.captureArea = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
        this.outputImageBytes = new ByteArrayOutputStream(500000);
        this.imageOutputStream = ImageIO.createImageOutputStream(this.outputImageBytes);
        this.screenshot = this.screenshotTaker.createScreenCapture(this.captureArea);
        this.socket = new Socket(this.address, this.port);
    }

    @Override
    public void run() throws IOException {
        this.inputStream = new DataInputStream(new BufferedInputStream(this.socket.getInputStream()));
        this.outputStream = new DataOutputStream(new BufferedOutputStream(this.socket.getOutputStream()));
        this.outputStream.write("<Request>".getBytes(StandardCharsets.UTF_8));
        this.outputStream.flush();
        this.inputStream.readNBytes(10);
        while(true){
            this.sendImage();
            this.inputStream.readNBytes(10);
            this.outputImageBytes.reset();
        }
    }
    
    public void sendImage() throws IOException {
        ImageIO.write(this.screenshotTaker.createScreenCapture(this.captureArea), "jpeg", this.outputImageBytes);
        this.fileSize = this.outputImageBytes.size();
        this.outputStream.writeInt(this.fileSize);
        this.outputStream.flush();
        this.inputStream.readNBytes(7);
        this.outputStream.write(this.outputImageBytes.toByteArray());
        this.outputStream.flush();
    }
}
