
package net.aer.module.modules.fun;

import net.aer.module.Category;
import net.aer.module.Module;
import net.aer.utils.valuesystem.BooleanValue;

public class AntiAim extends Module {

	public AntiAim() {
		super("AntiAim", Category.FUN, "Makes your head move as you wish!");
	}
        
	public void preUpdate(EventPreUpdate event) {
		minecraft.player.connection.sendPacket(new CPacketPlayer.Rotation(-90, -180));
	}
	
	public void onDisable() {
		minecraft.player.connection.sendPacket(new CPacketPlayer.Rotation(0, 0));
	}

}
