package net.aer.gui;

import net.aer.render.render2D.Fonts;
import net.aer.render.render2D.RenderUtils2D;
import net.aer.utils.world.TimerUtil;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.ResourceLocation;

import java.io.IOException;

import static net.aer.Aer.minecraft;


public class VanishGui extends GuiScreen {

    private int timeout = 10;
    private boolean closed = false;

    public VanishGui() {
        super();
        this.allowUserInput = false;
        try {
            TimerUtil.callAfter(10, this, this.getClass().getMethod("timeoutCounter", null));
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        ScaledResolution scaledRes = new ScaledResolution(minecraft);
        minecraft.getTextureManager().bindTexture(new ResourceLocation("aerassets/background.png"));
        Gui.drawModalRectWithCustomSizedTexture(0, 0, 0.0F, 0.0F, scaledRes.getScaledWidth(), scaledRes.getScaledHeight(), scaledRes.getScaledWidth(), scaledRes.getScaledHeight());
        RenderUtils2D.drawCenteredString(Fonts.massive, "Reloading world...", scaledRes.getScaledWidth() / 2, scaledRes.getScaledHeight() / 2, 0xaaffffff, true);
        if (timeout <= 7) {
            RenderUtils2D.drawCenteredString(Fonts.mid, "Timing out in: " + timeout, scaledRes.getScaledWidth() / 2, (scaledRes.getScaledHeight() / 2) + 45, 0xaaffffff, true);
        }
    }

    public void timeoutCounter() {
        timeout--;
        if (closed) {
            return;
        }
        if (timeout > 0) {
            try {
                TimerUtil.callAfter(10, this, this.getClass().getMethod("timeoutCounter", null));
            } catch (NoSuchMethodException e) {
            }
        } else {
            minecraft.displayGuiScreen(null);
            minecraft.world.sendQuittingDisconnectingPacket();
        }
    }

    @Override
    public void onGuiClosed() {
        super.onGuiClosed();
        this.closed = true;
    }

    /**
     * Overrides the ability to press esc to close the GUI. Instead, VanishGui runs a timer and will close from timeout if required
     *
     * @param typedChar
     * @param keyCode
     * @throws IOException
     */
    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
    }
}
