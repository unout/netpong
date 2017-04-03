package netpong;
import netpong.logics.LogicService;

public class NETpong {
    public static void main(String[] args) {

        javax.swing.SwingUtilities.invokeLater(LogicService::getInstance);

    }
}
