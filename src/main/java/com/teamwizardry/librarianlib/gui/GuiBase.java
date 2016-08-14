package com.teamwizardry.librarianlib.gui;

import java.io.IOException;

import net.minecraftforge.fml.client.config.GuiUtils;

import net.minecraft.client.gui.GuiScreen;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import com.teamwizardry.librarianlib.gui.components.ComponentVoid;
import com.teamwizardry.librarianlib.math.Vec2d;

public class GuiBase extends GuiScreen {
	protected ComponentVoid components;
	protected int top, left, guiWidth, guiHeight;
	
	public GuiBase(int guiWidth, int guiHeight) {
		components = new ComponentVoid(0, 0);
        components.setCalculateOwnHover(false);
        this.guiWidth = guiWidth;
        this.guiHeight = guiHeight;
	}
	
	@Override
	public void initGui() {
		super.initGui();
		left = width / 2 - guiWidth / 2;
        top = height / 2 - guiHeight / 2;
        
		if(components.getPos().xi != left || components.getPos().yi != top)
        	components.setPos(new Vec2d(left, top));
	}
	
	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		super.drawScreen(mouseX, mouseY, partialTicks);
		components.draw(components.relativePos(new Vec2d(mouseX, mouseY)), partialTicks);
		
		if(components.tooltipText != null) {
			GuiUtils.drawHoveringText(components.tooltipText, mouseX, mouseY, width, height, -1, components.tooltipFont == null ? mc.fontRendererObj : components.tooltipFont);
			components.tooltipText = null;
			components.tooltipFont = null;
		}
	}
	
	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
		super.mouseClicked(mouseX, mouseY, mouseButton);
        components.mouseDown(components.relativePos(new Vec2d(mouseX, mouseY)), EnumMouseButton.getFromCode(mouseButton));
	}
	
	@Override
	protected void mouseReleased(int mouseX, int mouseY, int state) {
		super.mouseReleased(mouseX, mouseY, state);
        components.mouseUp(components.relativePos(new Vec2d(mouseX, mouseY)), EnumMouseButton.getFromCode(state));
	}
	
	@Override
	protected void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick) {
		super.mouseClickMove(mouseX, mouseY, clickedMouseButton, timeSinceLastClick);
        components.mouseDrag(components.relativePos(new Vec2d(mouseX, mouseY)), EnumMouseButton.getFromCode(clickedMouseButton));
	}
	
	@Override
	public void handleKeyboardInput() throws IOException {
		super.handleKeyboardInput();
		
		if(Keyboard.getEventKeyState())
    		components.keyPressed(Keyboard.getEventCharacter(), Keyboard.getEventKey());
    	else
    		components.keyReleased(Keyboard.getEventCharacter(), Keyboard.getEventKey());
	}
	
	@Override
	public void handleMouseInput() throws IOException {
		super.handleMouseInput();
		int mouseX = Mouse.getEventX() * this.width / this.mc.displayWidth;
        int mouseY = this.height - Mouse.getEventY() * this.height / this.mc.displayHeight - 1;
    	int wheelAmount = Mouse.getEventDWheel();

        if (wheelAmount != 0)
        {
            if (wheelAmount > 0)
            {
                wheelAmount = 1;
            }

            if (wheelAmount < 0)
            {
                wheelAmount = -1;
            }
            
            components.mouseWheel(components.relativePos(new Vec2d(mouseX, mouseY)), wheelAmount);
        }
	}
}