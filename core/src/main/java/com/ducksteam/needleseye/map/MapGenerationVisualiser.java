package com.ducksteam.needleseye.map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.ducksteam.needleseye.Main.layout;
import static com.ducksteam.needleseye.Main.uiFont;

/**
 * Visualises the generation of a map.
 * Probably only used for debugging purposes
 * @author SkySourced
 */

public class MapGenerationVisualiser {

    record RoomPlacementData(String roomType, String templateName, int x, int y, int w, int h, int rotation, HashMap<Integer, Boolean> doors) {}

    ArrayList<String> instructions;
    int nextInstruction;
    Texture pixel;

    Color smallRoomColor = new Color(0x78b4ccff);
    Color hallwayColor = new Color(0xaeaeaeff);
    Color battleRoomColor = new Color(0xef5023ff);
    Color bossRoomColor = new Color(0xf90733ff);
    Color treasureRoomColor = new Color(0xfcd823ff);
    Color enabledDoorColor = new Color(0x20db20ff);
    Color disabledDoorColor = new Color(0xdb2020ff);
    Color selectedColor = new Color(0x20dbdbff);
    Color tryColor = new Color(0xfc523fff);

    private final int roomSize = 128;
    private final int selectedRoomSize = 96;
    RoomPlacementData selectedRoom;
    int selectedDoor;

    ArrayList<RoomPlacementData> rooms = new ArrayList<>();
    String[] recentMessage = {"" , "", ""};

    /**
     * Whether the visualiser has finished rendering, and the main game can begin
     */
    public boolean renderingComplete = false;

    Vector2 tmpPosVec = new Vector2();
    Vector2 tmpSizeVec = new Vector2();
    Vector2 tmpDoorVec = new Vector2();

    /**
     * Create a new map generation visualiser
     */
    public MapGenerationVisualiser() {
        instructions = new ArrayList<>();
        nextInstruction = 0;

        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.WHITE);
        pixmap.fill();
        pixel = new Texture(pixmap);
    }

    /**
     * Add an instruction to the visualiser.
     * Possible instructions are: <br> <ul>
     * <li>add-room (room type) (room name) (x) (y) (w) (h) (rotation)</li>
     * <li>select-room (x) (y)</li>
     * <li>select-door (door index)</li>
     * <li>try-position (room name) (x) (y) (w) (h) (rotation)</li>
     * <li>msg (message)</li>
     * </ul>
     *
     * @param instruction the instruction to add
     */
    public void addInstruction(String instruction) {
        instructions.add(instruction.toLowerCase());
    }

    /**
     * Mark an instruction as passed for rendering
     * @param count the number of instructions to mark as passed
     */
    public void step(int count){
        if (count > 0) {
            for (int i = 0; i < count; i++) {
                if (nextInstruction != instructions.size()) nextInstruction++;
                else if (instructions.size() > 1) {
                    renderingComplete = true;
                    break;
                }
            }
        } else if (count < 0) {
            for (int i = 0; i < -count; i++) {
                if (nextInstruction != 0) nextInstruction--;
            }
        }
        for (int i = 0; i < nextInstruction; i++) {
            if (i < instructions.size()) {
                Gdx.app.debug("Visualiser", instructions.get(i));
            }
        }
        renderingComplete = nextInstruction >= instructions.size();
    }

    /**
     * Render the visualiser
     * @param batch the sprite batch to render with
     */
    public void draw(SpriteBatch batch) {
        batch.begin();
        rooms.clear();
        recentMessage = new String[]{"", "", ""};

        // run the instructions
        for (int i = 0; i < nextInstruction; i++) {
            String instruction = instructions.get(i);
            String[] parts = instruction.split(" ");
            switch (parts[0]) {
                case "add-room" -> {
                    rooms.add(new RoomPlacementData(
                        parts[1], // room type
                        parts[2], // room name
                        Integer.parseInt(parts[3]), // x
                        Integer.parseInt(parts[4]), // y
                        Integer.parseInt(parts[5]), // w
                        Integer.parseInt(parts[6]), // h
                        Integer.parseInt(parts[7]), // rotation
                        MapManager.getRoomWithName(parts[2]).getDoors())); // doors

                    drawRoom(batch, rooms.getLast());
                }
                case "select-room" -> {
                    selectedRoom = rooms.stream().filter(room -> room.x == Integer.parseInt(parts[1]) && room.y == Integer.parseInt(parts[2])).findFirst().orElse(null);
                    if (selectedRoom == null) {
                        // algorithm has probably just selected a placeholder
                        if (!instructions.get(i + 1).equals("msg skipping hallway placeholder")) { // check next instruction
                            // if it hasn't there is a problem
                            throw new RuntimeException("Could not find room at " + parts[1] + ", " + parts[2]);
                        }
                        break;
                    }
                    selectRoom(batch, selectedRoom);
                }
                case "select-door" -> {
                    selectedDoor = Integer.parseInt(parts[1]);

                    selectDoor(batch, selectedDoor);
                }
                case "try-position" -> tryPos(batch, new RoomPlacementData(
                    null,
                    parts[1],
                    Integer.parseInt(parts[2]),
                    Integer.parseInt(parts[3]),
                    Integer.parseInt(parts[4]),
                    Integer.parseInt(parts[5]),
                    Integer.parseInt(parts[6]),
                    new HashMap<>()));
                case "msg" -> setRecentMessage(instruction.substring(4));
            }
        }

        // draw text labels for each room
        for (RoomPlacementData room : rooms) {
            uiFont.draw(batch, room.templateName.substring(0, 4), (float) Gdx.graphics.getWidth() /2 - (float) roomSize /2 + room.x*roomSize + 40, (float) Gdx.graphics.getHeight() /2 - (float) roomSize /2 + room.y*roomSize + (float) roomSize /2 + uiFont.getLineHeight()/2);
            uiFont.draw(batch, room.rotation + "", (float) Gdx.graphics.getWidth() /2 - (float) roomSize /2 + room.x*roomSize + 40, (float) Gdx.graphics.getHeight() /2 - (float) roomSize /2 + room.y*roomSize + (float) roomSize /2 - uiFont.getLineHeight()/2);
        }

        // draw more text
        uiFont.draw(batch, nextInstruction + "/" + instructions.size(), 10, Gdx.graphics.getHeight() - 10);
        uiFont.draw(batch, "Currently executing: " + (nextInstruction == 0 ? " " : instructions.get(nextInstruction-1)), 10, Gdx.graphics.getHeight() - 35);
        uiFont.draw(batch, "Use , and . to step through the instructions", 10, Gdx.graphics.getHeight() - 60);

        layout.setText(uiFont, recentMessage[0]);
        uiFont.draw(batch, recentMessage[0], (float) Gdx.graphics.getWidth() / 2 - layout.width / 2, Gdx.graphics.getHeight() - 70);
        layout.setText(uiFont, recentMessage[1]);
        uiFont.draw(batch, recentMessage[1], (float) Gdx.graphics.getWidth() / 2 - layout.width / 2, Gdx.graphics.getHeight() - 40);
        layout.setText(uiFont, recentMessage[2]);
        uiFont.draw(batch, recentMessage[2], (float) Gdx.graphics.getWidth() / 2 - layout.width / 2, Gdx.graphics.getHeight() - 10);

        uiFont.draw(batch, "-> +X", Gdx.graphics.getWidth() - 100, (float) Gdx.graphics.getHeight() / 2);
        uiFont.draw(batch, "^ +Z", (float) Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() - 130);

        batch.end();

        batch.setColor(Color.WHITE); // reset colour so you dont get blood load
    }

    private void updateColor(SpriteBatch batch, String colorType) {
        switch (colorType) {
            case "small" -> batch.setColor(smallRoomColor);
            case "hallway" -> batch.setColor(hallwayColor);
            case "battle" -> batch.setColor(battleRoomColor);
            case "boss" -> batch.setColor(bossRoomColor);
            case "treasure" -> batch.setColor(treasureRoomColor);
            case "enabled" -> batch.setColor(enabledDoorColor);
            case "disabled" -> batch.setColor(disabledDoorColor);
            case "try" -> batch.setColor(tryColor);
        }
    }

    private void drawRoom(SpriteBatch batch, RoomPlacementData room) {
        updateColor(batch, room.roomType);

        tmpPosVec.set((float) Gdx.graphics.getWidth() /2 - (float) roomSize /2 + room.x*roomSize,
            (float) Gdx.graphics.getHeight() /2 - (float) roomSize /2 + room.y*roomSize);
        tmpSizeVec.set(room.w * roomSize, room.h * roomSize).rotateDeg(room.rotation);

        if (tmpSizeVec.x < 0) tmpPosVec.x += roomSize;
        if (tmpSizeVec.y < 0) tmpPosVec.y += roomSize;

        batch.draw(pixel,
            tmpPosVec.x,
            tmpPosVec.y,
            tmpSizeVec.x,
            tmpSizeVec.y);

        for (Map.Entry<Integer, Boolean> door : room.doors.entrySet()) {
            if (door.getValue()) {
                drawDoor(batch, room, door.getKey(), enabledDoorColor);
            } else {
                drawDoor(batch, room, door.getKey(), disabledDoorColor);
            }
        }
    }

    private void drawDoor(SpriteBatch batch, RoomPlacementData room, int door, Color color) {
        batch.setColor(color);

        tmpPosVec.set((float) Gdx.graphics.getWidth() /2 - (float) roomSize /2 + room.x*roomSize,
            (float) Gdx.graphics.getHeight() /2 - (float) roomSize /2 + room.y*roomSize);

        tmpSizeVec.set(room.w * roomSize, room.h * roomSize).rotateDeg(room.rotation);

        tmpDoorVec.set(doorTranslations[door]).scl(roomSize).rotateDeg(room.rotation).add(tmpPosVec);

        if (tmpSizeVec.x < 0) tmpPosVec.x += roomSize;
        if (tmpSizeVec.y < 0) tmpPosVec.y += roomSize;

        if (tmpSizeVec.x < 0) tmpDoorVec.x += (float) (3 * roomSize) /4;
        if (tmpSizeVec.y < 0) tmpDoorVec.y += (float) (3 * roomSize) /4;

        batch.draw(pixel,
            tmpDoorVec.x,
            tmpDoorVec.y,
            (float) roomSize / 4,
            (float) roomSize / 4);
    }

    private final static Vector2[] doorTranslations = new Vector2[]{
        new Vector2(0.375f, 0f),
        new Vector2(0f, 0.375f),
        new Vector2(0.75f, 0.375f),
        new Vector2(0.375f, 0.75f),
        new Vector2(0f, 1.375f),
        new Vector2(0.75f, 1.375f),
        new Vector2(0.375f, 1.75f)
    };

    private void selectRoom(SpriteBatch batch, RoomPlacementData room) {
        redrawRooms(batch);

        batch.setColor(selectedColor);
        batch.draw(pixel,
            (float) Gdx.graphics.getWidth() /2 - (float) roomSize /2 + room.x*roomSize + (float) (roomSize - selectedRoomSize) /2,
            (float) Gdx.graphics.getHeight() /2 - (float) roomSize /2 + room.y*roomSize + (float) (roomSize - selectedRoomSize) /2,
            selectedRoomSize,
            selectedRoomSize);
    }

    private void tryPos(SpriteBatch batch, RoomPlacementData room) {
        redrawRooms(batch);

        updateColor(batch, "try");

        tmpPosVec.set((float) Gdx.graphics.getWidth() /2 - (float) roomSize /2 + room.x*roomSize,
            (float) Gdx.graphics.getHeight() /2 - (float) roomSize /2 + room.y*roomSize);
        tmpSizeVec.set(room.w * roomSize, room.h * roomSize).rotateDeg(room.rotation);

        if (tmpSizeVec.x < 0) tmpPosVec.x += roomSize;
        if (tmpSizeVec.y < 0) tmpPosVec.y += roomSize;

        batch.draw(pixel,
            tmpPosVec.x,
            tmpPosVec.y,
            tmpSizeVec.x,
            tmpSizeVec.y);

        for (Map.Entry<Integer, Boolean> door : room.doors.entrySet()) {
            if (door.getValue()) {
                drawDoor(batch, room, door.getKey(), enabledDoorColor);
            } else {
                drawDoor(batch, room, door.getKey(), disabledDoorColor);
            }
        }
    }

    private void selectDoor(SpriteBatch batch, int door) {
        redrawRooms(batch);

        drawDoor(batch, selectedRoom, door, selectedColor);
    }

    private void setRecentMessage(String message) {
        recentMessage[2] = recentMessage[1];
        recentMessage[1] = recentMessage[0];
        recentMessage[0] = message;
    }

    private void redrawRooms(SpriteBatch batch) {
        Gdx.gl32.glClearColor(0, 0, 0, 1);

        for (RoomPlacementData room : rooms) {
            drawRoom(batch, room);
        }
    }
}
