package net.aer.command.commands;

import com.darkmagician6.eventapi.EventManager;
import com.darkmagician6.eventapi.EventTarget;
import net.aer.Aer;
import net.aer.command.Command;
import net.aer.events.EventGuiOpened;
import net.aer.events.EventPacketRecieved;
import net.aer.gui.VanishGui;
import net.aer.render.render2D.ChatColour;
import net.aer.render.render2D.ChatUtils;
import net.aer.utils.world.WorldUtil;
import net.minecraft.client.gui.GuiDisconnected;
import net.minecraft.client.multiplayer.ServerAddress;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.network.NetHandlerLoginClient;
import net.minecraft.network.EnumConnectionState;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.handshake.client.C00Handshake;
import net.minecraft.network.login.client.CPacketLoginStart;
import net.minecraft.network.play.server.SPacketJoinGame;
import net.minecraft.util.text.TextComponentTranslation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;

public class Vanish extends Command {

    private static final Logger LOGGER = LogManager.getLogger();

    public Vanish() {
        super("Vanish", "Bungeecord exploit that attempts to make you invisible and invulnerable to other players", Aer.settings.getProperty("CmdPrefix", ".") + "vanish");

    }


    @Override
    public void onCommand(String command, String[] args) throws Exception {
        if (minecraft.isSingleplayer()) {
            ChatUtils.sendChatMessage("Unable to vanish in singleplayer", ChatColour.red, "Vanish", false);
            return;
        }
        EventManager.register(this);
        ArrayList<String> ignoredPlayers = WorldUtil.getPlayerNamesInRender();
        ArrayList<String> players = WorldUtil.getPlayerNamesList();
        for (String s : ignoredPlayers) {
            players.remove(s);
        }
        ServerData serverdata = minecraft.getCurrentServerData();
        ServerAddress serveraddress = ServerAddress.fromString(serverdata.serverIP);
        minecraft.displayGuiScreen(new VanishGui());
        ChatUtils.sendChatMessage("You should be invisible to:", ChatColour.green, "Vanish", true);
        for (String s : players) {
            ChatUtils.sendChatMessage(s, ChatColour.gray, "Vanish", false);
        }
        minecraft.loadWorldNoGui(null);
        minecraft.setServerData(serverdata);
        this.connect(serveraddress.getIP(), serveraddress.getPort());
    }

    private void connect(final String ip, final int port) {

        LOGGER.info("Connecting to {}, {}", ip, Integer.valueOf(port));
        (new Thread("Server Connector [AerVanish]") {
            public void run() {
                InetAddress inetaddress = null;

                try {
                    inetaddress = InetAddress.getByName(ip);
                    NetworkManager networkManager = NetworkManager.createNetworkManagerAndConnect(inetaddress, port, minecraft.gameSettings.isUsingNativeTransport());
                    networkManager.setNetHandler(new NetHandlerLoginClient(networkManager, minecraft, null));
                    networkManager.sendPacket(new C00Handshake(ip, port, EnumConnectionState.LOGIN));
                    networkManager.sendPacket(new CPacketLoginStart(minecraft.getSession().getProfile()));
                } catch (UnknownHostException unknownhostexception) {
                    LOGGER.error("Couldn't connect to server", (Throwable) unknownhostexception);
                    minecraft.displayGuiScreen(new GuiDisconnected(null, "connect.failed", new TextComponentTranslation("disconnect.genericReason", new Object[]{"Unknown host"})));
                } catch (Exception exception) {
                    LOGGER.error("Couldn't connect to server", (Throwable) exception);
                    String s = exception.toString();

                    if (inetaddress != null) {
                        String s1 = inetaddress + ":" + port;
                        s = s.replaceAll(s1, "");
                    }

                    minecraft.displayGuiScreen(new GuiDisconnected(null, "connect.failed", new TextComponentTranslation("disconnect.genericReason", new Object[]{s})));
                }
            }
        }).start();

    }

    @EventTarget
    public void onPacket(EventPacketRecieved event) {
        if (event.packet instanceof SPacketJoinGame) {
            SPacketJoinGame packet = (SPacketJoinGame) event.packet;
            event.doGui = false;
        }
    }

    @EventTarget
    public void onGui(EventGuiOpened event) {
        if (event.gui == null) {
            event.cancel();
            EventManager.unregister(this);
        }
    }

}
