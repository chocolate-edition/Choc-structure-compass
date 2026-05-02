package com.leclowndu93150.structurecompassfix.client.screen;

import com.leclowndu93150.structurecompassfix.network.NetworkHandler;
import com.leclowndu93150.structurecompassfix.network.SetEntriesPacket;
import com.leclowndu93150.structurecompassfix.util.CompassData;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.PacketDistributor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class CompassScreen extends Screen {

    private static final int PADDING = 6;

    private final InteractionHand hand;
    private final ItemStack compass;
    private final List<String> allEntries;
    private List<String> filteredEntries;
    private final Set<String> selected = new HashSet<>();

    private EntryList entryList;
    private EditBox search;
    private Button selectButton;

    public CompassScreen(InteractionHand hand, ItemStack compass, List<ResourceLocation> structures, List<ResourceLocation> tags) {
        super(Component.literal("Structure Compass"));
        this.hand = hand;
        this.compass = compass;

        this.allEntries = new ArrayList<>();
        structures.stream().sorted().forEach(loc -> allEntries.add(loc.toString()));
        tags.stream().sorted().forEach(loc -> allEntries.add("#" + loc));
        this.filteredEntries = new ArrayList<>(allEntries);

        List<String> existing = CompassData.getEntries(compass.getOrCreateTag());
        selected.addAll(existing);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    protected void init() {
        int centerX = width / 2;
        int listWidth = Math.min(width - 40, 300);

        int y = height - 20 - PADDING;
        addRenderableWidget(Button.builder(Component.translatable("gui.cancel"), b -> onClose())
                .bounds(centerX - listWidth / 2, y, listWidth, 20).build());

        y -= 24;
        selectButton = addRenderableWidget(Button.builder(Component.literal("Select (" + selected.size() + ")"), b -> {
            NetworkHandler.CHANNEL.send(PacketDistributor.SERVER.noArg(),
                    new SetEntriesPacket(hand, new ArrayList<>(selected)));
            if (minecraft.player != null) {
                minecraft.player.sendSystemMessage(
                        Component.literal("Selected " + selected.size() + " structure(s)").withStyle(ChatFormatting.GOLD));
            }
            onClose();
        }).bounds(centerX - listWidth / 2, y, listWidth, 20).build());
        selectButton.active = !selected.isEmpty();

        y -= 18;
        search = new EditBox(font, centerX - listWidth / 2 + 1, y, listWidth - 2, 14,
                Component.literal("Search..."));
        addWidget(search);
        search.setCanLoseFocus(true);
        search.setFocused(false);

        int listTop = PADDING + 24;
        int listBottom = y - font.lineHeight - PADDING;
        entryList = new EntryList(minecraft, listWidth, height, listTop, listBottom, font.lineHeight * 2 + 8);
        entryList.setLeftPos(centerX - listWidth / 2);
        addWidget(entryList);

        refreshList();
    }

    @Override
    public void tick() {
        search.tick();
        String filter = search.getValue().toLowerCase();
        filteredEntries = allEntries.stream()
                .filter(e -> e.toLowerCase().contains(filter))
                .collect(Collectors.toList());
        refreshList();
    }

    private void refreshList() {
        entryList.clear();
        for (String entry : filteredEntries) {
            entryList.add(entryList.new Entry(entry));
        }
    }

    private void toggleEntry(String entry) {
        if (selected.contains(entry)) {
            selected.remove(entry);
        } else {
            selected.add(entry);
        }
        selectButton.setMessage(Component.literal("Select (" + selected.size() + ")"));
        selectButton.active = !selected.isEmpty();
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partial) {
        this.renderDirtBackground(graphics);
        entryList.render(graphics, mouseX, mouseY, partial);

        graphics.drawCenteredString(font, "Search:", width / 2, search.getY() - font.lineHeight - 2, 0xFFFFFF);
        search.render(graphics, mouseX, mouseY, partial);

        super.render(graphics, mouseX, mouseY, partial);
    }

    @Override
    public boolean mouseClicked(double mx, double my, int button) {
        boolean result = super.mouseClicked(mx, my, button);
        if (button == 1 && search.isMouseOver(mx, my)) {
            search.setValue("");
        }
        return result;
    }

    public Font getFont() {
        return font;
    }

    class EntryList extends ObjectSelectionList<EntryList.Entry> {

        public EntryList(Minecraft mc, int width, int height, int top, int bottom, int itemHeight) {
            super(mc, width, height, top, bottom, itemHeight);
        }

        @Override
        protected int getScrollbarPosition() {
            return this.x1 - 6;
        }

        @Override
        public int getRowWidth() {
            return this.width - 12;
        }

        public void clear() {
            clearEntries();
        }

        public void add(Entry entry) {
            addEntry(entry);
        }

        class Entry extends ObjectSelectionList.Entry<Entry> {

            private final String entry;

            Entry(String entry) {
                this.entry = entry;
            }

            @Override
            public void render(GuiGraphics graphics, int index, int top, int left, int width, int height, int mouseX, int mouseY, boolean hovered, float partial) {
                boolean sel = selected.contains(entry);
                boolean isTag = CompassData.isTag(entry);
                String prefix = sel ? "\u2714 " : "  ";
                int color = sel ? 0x55FF55 : (isTag ? 0xFFAA00 : 0xFFFFFF);

                Component display = Component.literal(prefix + entry);
                Font f = CompassScreen.this.getFont();
                graphics.drawString(f, Language.getInstance().getVisualOrder(
                        FormattedText.composite(f.substrByWidth(display, width))),
                        left + 3, top + 6, color, false);
            }

            @Override
            public boolean mouseClicked(double mx, double my, int button) {
                toggleEntry(entry);
                return true;
            }

            @Override
            public Component getNarration() {
                return Component.literal(entry);
            }
        }
    }
}
