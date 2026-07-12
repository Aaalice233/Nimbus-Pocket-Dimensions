package net.nimbu.pocketdimensions.screen.widgets;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

public class InvisibleButton extends AbstractButton {
	protected final PressAction onPress;

	public InvisibleButton(int i, int j, int k, int l, PressAction onPress) {
		super(i, j, k, l, Component.empty());
		this.onPress = onPress;
	}

	@Override
	public void onPress() {
		this.onPress.onPress(this);
	}

	public static Builder builder(Component message, PressAction onPress) {
		return new Builder(message, onPress);
	}

	@Override
	protected void renderWidget(GuiGraphics context, int mouseX, int mouseY, float delta) {
	}

	@Override
	protected void updateWidgetNarration(NarrationElementOutput builder) {
	}

	@FunctionalInterface
	public interface PressAction {
		void onPress(InvisibleButton button);
	}

	public static class Builder {
		private final Component message;
		private final PressAction onPress;
		@Nullable
		private Tooltip tooltip;
		private int x;
		private int y;
		private int width = 150;
		private int height = 20;

		public Builder(Component message, PressAction onPress) {
			this.message = message;
			this.onPress = onPress;
		}

		public Builder position(int x, int y) {
			this.x = x;
			this.y = y;
			return this;
		}

		public Builder width(int width) {
			this.width = width;
			return this;
		}

		public Builder size(int width, int height) {
			this.width = width;
			this.height = height;
			return this;
		}

		public Builder dimensions(int x, int y, int width, int height) {
			return this.position(x, y).size(width, height);
		}

		public Builder tooltip(@Nullable Tooltip tooltip) {
			this.tooltip = tooltip;
			return this;
		}

		public InvisibleButton build() {
			InvisibleButton button = new InvisibleButton(this.x, this.y, this.width, this.height, this.onPress);
			button.setTooltip(this.tooltip);
			return button;
		}
	}
}
