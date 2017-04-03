package netpong.network;

import netpong.logics.LogicService;
import netpong.logics.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// TODO It is necessary to think about protocol
// TODO setIsConnected workaround copypaste
public class NetworkMessageParser {

    private static Pattern py = Pattern.compile("(PY)([0-9]{1,3})");
    private static Pattern px = Pattern.compile("(PX)([0-9]{1,3})");
    private static Pattern ps = Pattern.compile("(PS)([0-9]{1,3})");
    private static Pattern pc = Pattern.compile("(PC)([0-9]{1,3})");
    private static Pattern bx = Pattern.compile("(BX)([0-9]{1,3})");
    private static Pattern by = Pattern.compile("(BY)([0-9]{1,3})");
    private static Pattern rd = Pattern.compile("(REA)");
    private static Pattern pu = Pattern.compile("(PAU)");
    private static Pattern ok = Pattern.compile("(OK)");
    private static Pattern pa[] = {py, px, ps, pc, bx, by, rd, pu, ok};

    public static void parse(String message) {
        Packet packet = check(message);

        for (Map.Entry<String, String> entry : packet.getDictionary().entrySet()) {

            Map<String, Consumer<Integer>> actions;

            if (packet.isServer()) {
                actions = serverPacketActions;
            } else {
                actions = clientPacketActions;
            }

            if (actions.containsKey(entry.getKey())) {
                int val = Integer.parseInt(entry.getValue());
                actions.get(entry.getKey()).accept(val);
            }
        }
    }
    // look in LogicService
    // TODO check message (regex) !!
    // TODO debug
    // TODO length-1
    private static Packet check(String message) {
        Packet packet = new Packet();
        packet.setIsServer(message.charAt(0) == 'S');
        for (Pattern p : pa) {
            Matcher m = p.matcher(message);
            if (m.find()) {
                if (Objects.equals(m.group(0), m.group(1))) {
                    packet.addToDictionary(m.group(1), "0");
                } else {
                    packet.addToDictionary(m.group(1), m.group(2));
                }
            }
        }
        return packet;
    }
    private static Packet prepare(String message) {
        Packet packet = new Packet();
        packet.setIsServer(message.charAt(0) == 'S');
        for (int i = 1; i < message.length(); i += 6) {
            String block = message.substring(i, i + 6);
            packet.addToDictionary(block.substring(0, 3), block.substring(3, 6));
        }
        return packet;
    }

    private static class Packet {
        private boolean isServer;

        private Map<String, String> dictionary = new HashMap<>(); // why?

        public Map<String, String> getDictionary() {
            return dictionary;
        }

        public void addToDictionary(String key, String val) {
            dictionary.put(key, val);
        }

        public boolean isServer() {
            return isServer;
        }

        public void setIsServer(boolean isServer) {
            this.isServer = isServer;
        }
    }
    // dictionary, server(client)PacketAction ? three maps
    private static Map<String, Consumer<Integer>> serverPacketActions;
    private static Map<String, Consumer<Integer>> clientPacketActions;

    // TODO use enum instead of string value "BAX", "BAY" ...
    // mb list?

    static {
        serverPacketActions = new HashMap<>();
        serverPacketActions.put("BX", val -> {
            LogicService
                    .getInstance()
                    .getBall()
                    .setX(val);
            LogicService
                    .getInstance()
                    .setIsConnected(true);
        });
        serverPacketActions.put("BY", val -> {
            LogicService
                    .getInstance()
                    .getBall()
                    .setY(val);
            LogicService
                    .getInstance()
                    .setIsConnected(true);
        });
        serverPacketActions.put("PY", val -> {
            Player player =
                    LogicService
                            .getInstance()
                            .getPlayers()
                            .stream()
                            .filter(Player::isServer)
                            .findFirst()
                            .get();
            player.setIsReady(true);
            player.setY(val);
            LogicService
                    .getInstance()
                    .setIsConnected(true);

        });
        serverPacketActions.put("PX", val -> {
            Player player =
                    LogicService
                            .getInstance()
                            .getPlayers()
                            .stream()
                            .filter(Player::isServer)
                            .findFirst()
                            .get();
            player.setIsReady(true);
            player.setX(val);
            LogicService
                    .getInstance()
                    .setIsConnected(true);

        });
        serverPacketActions.put("PS", val -> {
            Player ps =
                    LogicService
                            .getInstance()
                            .getPlayers()
                            .stream()
                            .filter(Player::isServer)
                            .findFirst()
                            .get();

            ps.setPoints(val);
            ps.setIsReady(false);
            LogicService
                    .getInstance()
                    .setIsConnected(true);

        });
        serverPacketActions.put("PC", val -> {
            Player pc =
                    LogicService
                            .getInstance()
                            .getPlayers()
                            .stream()
                            .filter(x -> !x.isServer())
                            .findFirst()
                            .get();

            pc.setPoints(val);
            pc.setIsReady(false);
            LogicService
                    .getInstance()
                    .setIsConnected(true);

        });
        serverPacketActions.put("PAU", val -> {
            if (LogicService.getInstance().isActive()) {
                LogicService.getInstance().pause();
                LogicService.getInstance().getForm().getFrame().setTitle("NETpong PAUSED");
            } else {
                LogicService.getInstance().resume();
                LogicService.getInstance().getForm().getFrame().setTitle("NETpong");
            }
            LogicService.getInstance().setIsConnected(true);
        });
        serverPacketActions.put("OK", val ->
            LogicService.getInstance().setIsConnected(true));

        serverPacketActions.put("REA", val -> {
            LogicService
                    .getInstance()
                    .getPlayers()
                    .stream()
                    .filter(Player::isServer)
                    .findFirst()
                    .get()
                    .setIsReady(true);
            LogicService
                    .getInstance()
                    .setIsConnected(true);
        });

        clientPacketActions = new HashMap<>();

        clientPacketActions.put( "PY", val -> {
            Player player =
                    LogicService
                            .getInstance()
                            .getPlayers()
                            .stream()
                            .filter(x -> !x.isServer())
                            .findFirst()
                            .get();
            player.setIsReady(true);
            player.setY(val);
        });
        clientPacketActions.put( "PX", val -> {
            Player player =
                    LogicService
                            .getInstance()
                            .getPlayers()
                            .stream()
                            .filter(x -> !x.isServer())
                            .findFirst()
                            .get();
            player.setIsReady(true);
            player.setX(val);
        });
        clientPacketActions.put( "REA", val ->
            LogicService
                    .getInstance()
                    .getPlayers()
                    .stream()
                    .filter(x -> !x.isServer())
                    .findFirst()
                    .get()
                    .setIsReady(true));

        clientPacketActions.put( "OK", val ->
            LogicService.getInstance().setIsConnected(true));

        clientPacketActions.put( "PAU", val -> {
            if (LogicService.getInstance().isActive()) {
                LogicService.getInstance().pause();
                LogicService.getInstance().getForm().getFrame().setTitle("NETpong PAUSED");
            } else {
                LogicService.getInstance().resume();
                LogicService.getInstance().getForm().getFrame().setTitle("NETpong");
            }
        });

    }

}
