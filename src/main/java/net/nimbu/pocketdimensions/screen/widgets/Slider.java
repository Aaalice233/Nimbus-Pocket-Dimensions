package net.nimbu.pocketdimensions.screen.widgets;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import org.lwjgl.glfw.GLFW;

import java.util.List;
import java.util.function.Consumer;

import static net.nimbu.pocketdimensions.screen.custom.DimensionCustomizerScreen.SLIDER_KNOB;

public class Slider extends AbstractWidget {
	private final int min = 1;
	private final int max;
	private int value;
	private boolean mouseEnteredWithClick;
	private boolean mouseOutLastFrame;
	private boolean held;
	private final Component name;
	private final EditBox textBox;

	public Slider(int x, int y, int width, int height, Component objName, int initial, int max) {
		super(x, y, width, height, Component.empty());
		name = objName;
		this.max = max;
		this.value = initial;
		textBox = new EditBox(Minecraft.getInstance().font, x + width + 3, y - 11, 20, 17, Component.literal(initial + ""));
		textBox.setValue(initial + "");
	}

	@Override
	protected void renderWidget(GuiGraphics context, int mouseX, int mouseY, float delta) {
		boolean mouseDown = GLFW.glfwGetMouseButton(Minecraft.getInstance().getWindow().getWindow(), GLFW.GLFW_MOUSE_BUTTON_1) == GLFW.GLFW_PRESS;
		if (mouseOutLastFrame && isHovered() && mouseDown) mouseEnteredWithClick = true;
		if (!mouseDown) mouseEnteredWithClick = false;
		if (mouseDown && !mouseEnteredWithClick && isHovered()) held = true;
		if (!mouseDown && held) held = false;
		if (held) this.updateValue(mouseX);
		else if (this.textBox.getValue().matches("[0-9]+") && Integer.parseInt(textBox.getValue()) != this.getValue()) {
			this.setValue(Integer.parseInt(textBox.getValue()));
		}

		context.drawString(Minecraft.getInstance().font, name.getString(), getX(), getY() - 10, 0xFFFFFF, false);
		context.fill(getX(), getY() + 2, getX() + width - 1, getY() + 5, 0xFF404040);
		int knobX = getX() + (int) ((value - min) / (float) (max - min) * (width - 8));
		context.blit(SLIDER_KNOB, knobX, getY(), 0, 0, 7, 7, 7, 7);
		mouseOutLastFrame = !isHovered();
	}

	@Override
	protected void updateWidgetNarration(NarrationElementOutput builder) {
	}

	private void updateValue(double mouseX) {
		double t = (mouseX - getX()) / (double) (width - 5);
		t = Math.clamp(t, 0.0, 1.0);
		value = min + (int) Math.round(t * (max - min));
		textBox.setValue(getValue() + "");
	}

	public void setValue(int x) {
		x = Math.clamp(x, min, max);
		value = x;
		textBox.setValue(getValue() + "");
	}

	public int getValue() {
		return value;
	}

	public boolean getVisibility() {
		return this.visible;
	}

	public void setVisibility(boolean visibility) {
		visitWidgets(w -> w.visible = visibility);
	}

	public void visitWidgets(Consumer<AbstractWidget> consumer) {
		for (AbstractWidget child : List.of(this, textBox)) {
			consumer.accept(child);
		}
	}
}
