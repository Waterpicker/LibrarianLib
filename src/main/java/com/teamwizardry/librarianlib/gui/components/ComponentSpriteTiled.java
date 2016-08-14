package com.teamwizardry.librarianlib.gui.components;

import org.lwjgl.opengl.GL11;

import com.teamwizardry.librarianlib.gui.GuiComponent;
import com.teamwizardry.librarianlib.gui.Option;
import com.teamwizardry.librarianlib.util.Color;
import com.teamwizardry.librarianlib.sprite.DrawingUtil;
import com.teamwizardry.librarianlib.sprite.Sprite;
import com.teamwizardry.librarianlib.math.Vec2d;

public class ComponentSpriteTiled extends GuiComponent<ComponentSpriteTiled> {
	
	public Option<ComponentSpriteTiled, Boolean> depth = new Option<>(true);
	public Option<ComponentSpriteTiled, Color> color = new Option<>(Color.WHITE);
	
	protected int borderSize = 3;
	
	protected Sprite topLeft, topRight, bottomLeft, bottomRight;
	protected Sprite top, right, bottom, left;
	protected Sprite middle, main;
	
	public ComponentSpriteTiled(Sprite sprite, int borderSize, int x, int y) {
		this(sprite, borderSize, x, y, sprite.width, sprite.height);
	}
	
	public ComponentSpriteTiled(Sprite sprite, int borderSize, int x, int y, int width, int height) {
		super(x, y, width, height);
		this.main = sprite;
		this.borderSize = borderSize;
		
		int insideU = main.width-borderSize;
		int insideV = main.height-borderSize;
		
		this.topLeft = main.getSubSprite(0, 0, borderSize, borderSize);
		this.topRight = main.getSubSprite(insideU, 0, borderSize, borderSize);
		
		this.bottomLeft = main.getSubSprite(0, insideV, borderSize, borderSize);
		this.bottomRight = main.getSubSprite(insideU, insideV, borderSize, borderSize);
		
		this.top = main.getSubSprite(borderSize, 0, main.width-(2*borderSize), borderSize);
		this.bottom = main.getSubSprite(borderSize, insideV, main.width-(2*borderSize), borderSize);
		
		this.left = main.getSubSprite(0, borderSize, borderSize, main.height-(2*borderSize));
		this.right = main.getSubSprite(insideU, borderSize, borderSize, main.height-(2*borderSize));
		
		this.middle = main.getSubSprite(borderSize, borderSize, main.width-(2*borderSize), main.height-(2*borderSize));
	}
	
	@Override
	public void drawComponent(Vec2d mousePos, float partialTicks) {
		boolean alwaysTop = !depth.getValue(this);
		
		if(alwaysTop) {
			// store the current depth function
			GL11.glPushAttrib(GL11.GL_DEPTH_BUFFER_BIT);

			// by using GL_ALWAYS instead of disabling depth it writes to the depth buffer
			// imagine a mountain, that is the depth buffer. this causes the sprite to write
			// it's value to the depth buffer, cutting a hole down wherever it's drawn.
			GL11.glDepthFunc(GL11.GL_ALWAYS);
		}
		color.getValue(this).glColor();
		main.getTex().bind();
		draw(pos.xf, pos.yf, size.xi, size.yi);
		
		if(alwaysTop)
			GL11.glPopAttrib();
	}
	
	public void draw(float x, float y, int width, int height) {
		DrawingUtil.startDrawingSession();
		
		float insideX = x+width-borderSize;
		float insideY = y+height-borderSize;
		
		topLeft.draw(getAnimationTicks(), x, y, borderSize, borderSize);
		
		topRight.draw(getAnimationTicks(), insideX, y, borderSize, borderSize);
		
		bottomLeft.draw(getAnimationTicks(), x, insideY, borderSize, borderSize);
		bottomRight.draw(getAnimationTicks(), insideX, insideY, borderSize, borderSize);
		
		left.drawClipped(getAnimationTicks(), x, borderSize, borderSize, height-(2*borderSize));
		right.drawClipped(getAnimationTicks(), insideX, borderSize, borderSize, height-(2*borderSize));
		
		top.drawClipped(getAnimationTicks(), borderSize, 0, width-(2*borderSize), borderSize);
		bottom.drawClipped(getAnimationTicks(), borderSize, insideY, width-(2*borderSize), borderSize);

		middle.drawClipped(getAnimationTicks(), borderSize, borderSize, width-(2*borderSize), height-(2*borderSize));
		
		DrawingUtil.endDrawingSession();
	}

}