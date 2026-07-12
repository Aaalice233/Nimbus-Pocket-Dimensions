package net.nimbu.pocketdimensions.screen.widgets;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import org.lwjgl.glfw.GLFW;

import static net.nimbu.pocketdimensions.screen.custom.DimensionCustomizerScreen.SLIDER_KNOB;

public class ColourSlider extends AbstractWidget {
	private final int min = 0;
	private final int max = 255;
	private int value;
	private boolean mouseEnteredWithClick;
	private boolean mouseOutLastFrame;
	private boolean held;
	private final int colour;
	private final Component name;
	private ColourSlider rSlider;
	private ColourSlider gSlider;
	private ColourSlider bSlider;
	private EditBox rTextBox;
	private EditBox gTextBox;
	private EditBox bTextBox;
	private int lowGrad;
	private int highGrad;

	public ColourSlider(int x, int y, int width, int height, Component objName, int initial, int colour) {
		super(x, y, width, height, Component.empty());
		name = objName;
		this.colour = colour;
		this.value = initial;
	}

	public void setOtherColourSliders(
			ColourSlider r, ColourSlider g, ColourSlider b,
			EditBox rText, EditBox gText, EditBox bText) {
		rSlider = r;
		gSlider = g;
		bSlider = b;
		rSlider.setGrads((gSlider.getValue() << 8) | bSlider.getValue(),
				(255 << 16) | (gSlider.getValue() << 8) | bSlider.getValue());
		gSlider.setGrads((rSlider.getValue() << 16) | bSlider.getValue(),
				(rSlider.getValue() << 16) | (255 << 8) | bSlider.getValue());
		bSlider.setGrads((rSlider.getValue() << 16) | (gSlider.getValue() << 8),
				(rSlider.getValue() << 16) | (gSlider.getValue() << 8) | 255);

		rTextBox = rText;
		gTextBox = gText;
		bTextBox = bText;
		rTextBox.setValue(rSlider.getValue() + "");
		gTextBox.setValue(gSlider.getValue() + "");
		bTextBox.setValue(bSlider.getValue() + "");
	}

	@Override
	protected void renderWidget(GuiGraphics context, int mouseX, int mouseY, float delta) {
		boolean mouseDown = GLFW.glfwGetMouseButton(Minecraft.getInstance().getWindow().getWindow(), GLFW.GLFW_MOUSE_BUTTON_1) == GLFW.GLFW_PRESS;
		if (mouseOutLastFrame && isHovered() && mouseDown) mouseEnteredWithClick = true;
		if (!mouseDown) mouseEnteredWithClick = false;
		if (mouseDown && !mouseEnteredWithClick && isHovered()) held = true;
		if (!mouseDown && held) held = false;
		if (held) updateValue(mouseX);
		else if (rTextBox.getValue().matches("[0-9]+") && Integer.parseInt(rTextBox.getValue()) != rSlider.getValue()) {
			rSlider.setValue(Integer.parseInt(rTextBox.getValue()));
		} else if (gTextBox.getValue().matches("[0-9]+") && Integer.parseInt(gTextBox.getValue()) != gSlider.getValue()) {
			gSlider.setValue(Integer.parseInt(gTextBox.getValue()));
		} else if (bTextBox.getValue().matches("[0-9]+") && Integer.parseInt(bTextBox.getValue()) != bSlider.getValue()) {
			bSlider.setValue(Integer.parseInt(bTextBox.getValue()));
		}

		context.drawString(Minecraft.getInstance().font, name.getString(), getX() - 14, getY(), colour, false);
		drawHorizontalGradient(context, getX(), getY(), width, height, lowGrad, highGrad);

		float brightness = (float) (1 - ((double) value / (double) max) * 0.5);
		int knobX = getX() + (int) ((value - min) / (float) (max - min) * (width - 8));
		RenderSystem.setShaderColor(brightness, brightness, brightness, 1.0f);
		context.blit(SLIDER_KNOB, knobX, getY(), 0, 0, 7, 7, 7, 7);
		mouseOutLastFrame = !isHovered();
		RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
	}

	private void drawHorizontalGradient(GuiGraphics context, int x, int y, int width, int height, int leftColor, int rightColor) {
		for (int i = 0; i < width - 2; i += 3) {
			float t = i / (float) (width - 3);
			int r = Math.round(lerp(t, (leftColor >> 16) & 0xFF, (rightColor >> 16) & 0xFF));
			int g = Math.round(lerp(t, (leftColor >> 8) & 0xFF, (rightColor >> 8) & 0xFF));
			int b = Math.round(lerp(t, leftColor & 0xFF, rightColor & 0xFF));
			int color = 0xFF000000 | (r << 16) | (g << 8) | b;
			context.fill(x + i, y + 2, x + i + 3, y + 5, color);
		}
	}

	float lerp(float t, int a, int b) {
		return a + t * (b - a);
	}

	@Override
	protected void updateWidgetNarration(NarrationElementOutput builder) {
	}

	private void updateValue(double mouseX) {
		double t = (mouseX - getX()) / (double) (width - 5);
		t = Math.clamp(t, 0.0, 1.0);
		value = min + (int) Math.round(t * (max - min));
		syncGradsAndText();
	}

	public void setValue(int x) {
		x = Math.clamp(x, min, max);
		value = x;
		syncGradsAndText();
	}

	private void syncGradsAndText() {
		rSlider.setGrads((gSlider.getValue() << 8) | bSlider.getValue(),
				(255 << 16) | (gSlider.getValue() << 8) | bSlider.getValue());
		gSlider.setGrads((rSlider.getValue() << 16) | bSlider.getValue(),
				(rSlider.getValue() << 16) | (255 << 8) | bSlider.getValue());
		bSlider.setGrads((rSlider.getValue() << 16) | (gSlider.getValue() << 8),
				(rSlider.getValue() << 16) | (gSlider.getValue() << 8) | 255);
		rTextBox.setValue(rSlider.getValue() + "");
		gTextBox.setValue(gSlider.getValue() + "");
		bTextBox.setValue(bSlider.getValue() + "");
	}

	public void setGrads(int low, int high) {
		lowGrad = 0xFF000000 | (low & 0x00FFFFFF);
		highGrad = 0xFF000000 | (high & 0x00FFFFFF);
	}

	public int getValue() {
		return value;
	}
}
