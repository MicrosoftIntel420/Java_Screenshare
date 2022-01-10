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

    private Robot screenshotTaker;
    private Rectangle captureArea;
    private BufferedImage screenshot;
    private ByteArrayOutputStream outputImageBytes;
    private ImageOutputStream imageOutputStream;

    private ImageWriter compressor;
    private ImageWriteParam compressionSettings;
    private IIOImage iioimage;


    public Client(String address, int port) throws IOException, AWTException {
        this.address = InetAddress.getByName(address);
        this.port = port;
        this.screenshotTaker = new Robot();
        this.captureArea = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
        this.outputImageBytes = new ByteArrayOutputStream(300000);
        this.imageOutputStream = ImageIO.createImageOutputStream(this.outputImageBytes);

        this.compressor = ImageIO.getImageWritersByFormatName("jpg").next();
        this.compressor.setOutput(this.imageOutputStream);
        this.compressionSettings = this.compressor.getDefaultWriteParam();
        this.compressionSettings.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
        this.compressionSettings.setCompressionQuality(0.35f);
        this.screenshot = this.screenshotTaker.createScreenCapture(this.captureArea);
        this.iioimage = new IIOImage(this.screenshot, null, null);

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
        this.screenshot = this.screenshotTaker.createScreenCapture(this.captureArea);
        ImageIO.write(this.screenshot, "jpg", this.outputImageBytes);
        this.fileSize = this.outputImageBytes.size();
        this.outputStream.writeInt(this.fileSize);
        this.outputStream.flush();
        this.inputStream.readNBytes(7);
        this.outputStream.write(this.outputImageBytes.toByteArray());
        this.outputStream.flush();
    }
}
