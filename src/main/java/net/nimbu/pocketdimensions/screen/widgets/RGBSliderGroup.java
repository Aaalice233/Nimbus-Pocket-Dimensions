package net.nimbu.pocketdimensions.screen.widgets;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.network.chat.Component;

import java.util.List;
import java.util.function.Consumer;

public class RGBSliderGroup implements Renderable {
	private final ColourSlider rSlider;
	private final ColourSlider gSlider;
	private final ColourSlider bSlider;
	private final EditBox rTextBox;
	private final EditBox gTextBox;
	private final EditBox bTextBox;

	protected int width;
	protected int height;
	private int x;
	private int y;

	public RGBSliderGroup(int x, int y, int width, int height, int borderWidth, int barSeparation, int[] initialValues) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		final int textBoxWidth = 26;
		final int textBoxHeight = 12;
		int sliderWidth = width - (borderWidth * 3) - textBoxWidth;
		int sliderHeight = (height - ((borderWidth + barSeparation) * 2)) / 3;
		int redY = y + borderWidth;
		int greenY = y + borderWidth + sliderHeight + barSeparation;
		int blueY = y + borderWidth + 2 * (sliderHeight + barSeparation);

		rSlider = new ColourSlider(x + borderWidth, redY, sliderWidth - 1, sliderHeight, Component.literal("R"), initialValues[0], 0xFF0000);
		gSlider = new ColourSlider(x + borderWidth, greenY, sliderWidth - 1, sliderHeight, Component.literal("G"), initialValues[1], 0x00FF00);
		bSlider = new ColourSlider(x + borderWidth, blueY, sliderWidth - 1, sliderHeight, Component.literal("B"), initialValues[2], 0x0000FF);

		rTextBox = new EditBox(Minecraft.getInstance().font, x + borderWidth + sliderWidth + barSeparation, redY - 2, textBoxWidth, textBoxHeight, Component.literal("R"));
		rTextBox.setMaxLength(3);
		rTextBox.setValue(rSlider.getValue() + "");

		gTextBox = new EditBox(Minecraft.getInstance().font, x + borderWidth + sliderWidth + barSeparation, greenY - 2, textBoxWidth, textBoxHeight, Component.literal("G"));
		gTextBox.setMaxLength(3);
		gTextBox.setValue(gSlider.getValue() + "");

		bTextBox = new EditBox(Minecraft.getInstance().font, x + borderWidth + sliderWidth + barSeparation, blueY - 2, textBoxWidth, textBoxHeight, Component.literal("B"));
		bTextBox.setMaxLength(3);
		bTextBox.setValue(bSlider.getValue() + "");

		rSlider.setOtherColourSliders(rSlider, gSlider, bSlider, rTextBox, gTextBox, bTextBox);
		gSlider.setOtherColourSliders(rSlider, gSlider, bSlider, rTextBox, gTextBox, bTextBox);
		bSlider.setOtherColourSliders(rSlider, gSlider, bSlider, rTextBox, gTextBox, bTextBox);
	}

	@Override
	public void render(GuiGraphics context, int mouseX, int mouseY, float delta) {
	}

	public int[] getColour() {
		return new int[]{rSlider.getValue(), gSlider.getValue(), bSlider.getValue()};
	}

	public void visitWidgets(Consumer<AbstractWidget> consumer) {
		for (AbstractWidget child : List.of(rSlider, gSlider, bSlider, rTextBox, gTextBox, bTextBox)) {
			consumer.accept(child);
		}
	}

	public boolean getVisibility() {
		return rSlider.visible;
	}

	public void setVisibility(boolean visibility) {
		visitWidgets(w -> w.visible = visibility);
	}
}
