package netpong.logics;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

class UserService implements KeyListener {


    @Override
    public void keyTyped(KeyEvent e) {

        if (e.getKeyChar() == ' ') {
            if (LogicService.getInstance().isActive()) {
                LogicService.getInstance().pause();
                LogicService.getInstance().getForm().getFrame().setTitle("NETpong PAUSED");
            } else {
                LogicService.getInstance().resume();
                LogicService.getInstance().getForm().getFrame().setTitle("NETpong");
            }
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        keyProcess(e, true);
    }

    @Override
    public void keyReleased(KeyEvent e) {
        keyProcess(e, false);
    }


    private void keyProcess(KeyEvent e, boolean isPressed) {
        LogicService service = LogicService.getInstance();
        service.getPlayers()
                .stream()
                .filter(player -> !service.isNetwork()
                                    || (service.isNetwork() && service.isServer() == player.isServer()))
                .forEach(player -> {
                    if (e.getKeyChar() == player.getDownChar()) {
                        player.setIsDownPressed(isPressed);
                        player.setIsReady(true);
                    }
                    if (e.getKeyChar() == player.getUpChar()) {
                        player.setIsUpPressed(isPressed);
                        player.setIsReady(true);
                    }
                    if (e.getKeyChar() == player.getBackChar()) {
                        player.setIsBackPressed(isPressed);
                        player.setIsReady(true);
                    }
                    if (e.getKeyChar() == player.getForwardChar()) {
                        player.setIsForwardPressed(isPressed);
                        player.setIsReady(true);
                    }
                });
    }
}