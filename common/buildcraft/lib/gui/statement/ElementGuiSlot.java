package buildcraft.lib.gui.statement;

import java.util.List;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;

import buildcraft.api.core.render.ISprite;
import buildcraft.api.statements.IGuiSlot;

import buildcraft.lib.gui.GuiElementSimple;
import buildcraft.lib.gui.GuiIcon;
import buildcraft.lib.gui.elem.ToolTip;
import buildcraft.lib.gui.pos.GuiRectangle;
import buildcraft.lib.gui.pos.IGuiArea;
import buildcraft.lib.gui.pos.IGuiPosition;
import buildcraft.lib.misc.data.IReference;

public abstract class ElementGuiSlot<G extends GuiStatementSelector<?>, T extends IGuiSlot> extends GuiElementSimple<G> {

    /** An array containing [offset][X|Y] */
    private static final int[][] OFFSET_HOVER = {
        // First 8
        { -1, -1 }, { 0, -1 }, { 1, -1 }, { 1, 0 }, { 1, 1 }, { 0, 1 }, { -1, 1 }, { -1, 0 },
        // Top row + going down
        { -2, -2 }, { -1, -2 }, { 0, -2 }, { 1, -2 }, { 2, -2 }, { 2, -1 }, { 2, 0 }, { 2, 1 },
        // Bottom row + going up
        { 2, 2 }, { 1, 2 }, { 0, 2 }, { -1, 2 }, { -2, 2 }, { -2, 1 }, { -2, 0 }, { -2, -1 }

    };

    private T[] possible = null;
    private IGuiArea[] posPossible = null;

    public final IReference<T> reference;

    public ElementGuiSlot(G gui, IGuiArea element, IReference<T> reference) {
        super(gui, element);
        this.reference = reference;
    }

    @Override
    public void addToolTips(List<ToolTip> tooltips) {
        if (possible != null) {
            for (int i = 0; i < possible.length; i++) {
                if (possible[i] != null && posPossible[i].contains(gui.mouse)) {
                    addToolTip(possible[i], tooltips);
                    break;
                }
            }
        }
        if (gui.currentHover != null && gui.currentHover != this) return;
        T value = reference.get();
        if (value != null && contains(gui.mouse)) {
            addToolTip(value, tooltips);
        }
    }

    protected void addToolTip(T value, List<ToolTip> tooltips) {
        String desc = value.getDescription();
        if (desc != null && desc.length() > 0) {
            tooltips.add(new ToolTip(desc));
        }
    }

    @Override
    public void drawBackground(float partialTicks) {
        draw(reference.get(), this);
    }

    @Override
    public void drawForeground(float partialTicks) {
        if (possible != null) {
            GlStateManager.disableDepth();
            int sub = 18 * (possible.length > 8 ? 2 : 1);
            int add = 18 * (possible.length > 8 ? 3 : 1);
            int x = getX() - sub - 4;
            int y = getY() - sub - 4;
            int size = 8 + add + 18 * 2;
            GuiStatementSelector.SELECTION_HOVER.draw(x, y, size, size);
            for (int i = 0; i < possible.length; i++) {
                draw(possible[i], posPossible[i]);
            }
            draw(reference.get(), this);
            GlStateManager.enableDepth();
        }
    }

    public void draw(T val, IGuiPosition position) {
        draw(gui, val, position);
    }

    public static void draw(Gui gui, IGuiSlot slot, IGuiPosition pos) {
        if (slot != null) {
            ISprite sprite = slot.getGuiSprite();
            if (sprite != null) {
                GuiIcon.drawAt(sprite, pos.getX() + 1, pos.getY() + 1, 16, 16);
            }
        }
    }

    protected abstract T[] getPossible();

    @Override
    public void onMouseClicked(int button) {
        if (contains(gui.mouse)) {
            if (GuiScreen.isShiftKeyDown()) {
                reference.set(null);
            } else {
                displayPossible();
            }
        }
    }

    public void displayPossible() {
        if (gui.currentHover != null) {
            return;
        }
        possible = getPossible();
        if (possible != null) {
            gui.currentHover = this;
            posPossible = new IGuiArea[possible.length];
            for (int i = 0; i < possible.length; i++) {
                if (i < OFFSET_HOVER.length) {
                    posPossible[i] = offset(OFFSET_HOVER[i][0] * 18, OFFSET_HOVER[i][1] * 18);
                } else {
                    // Too many elements, they will go offscreen
                    posPossible[i] = new GuiRectangle(-200, -200, 18, 18);
                }
            }
        }
    }

    @Override
    public void onMouseReleased(int button) {
        gui.currentHover = null;
        if (possible != null) {
            for (int i = 0; i < possible.length; i++) {
                if (posPossible[i].contains(gui.mouse)) {
                    reference.set(possible[i]);
                    break;
                }
            }
        }

        possible = null;
        posPossible = null;
    }
}
