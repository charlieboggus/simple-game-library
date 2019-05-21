package com.github.charlieboggus.sgl.input;

import static org.lwjgl.glfw.GLFW.*;

public enum Keys
{
    Escape(GLFW_KEY_ESCAPE, "Escape"),
    F1(GLFW_KEY_F1, "F1"),
    F2(GLFW_KEY_F2, "F2"),
    F3(GLFW_KEY_F3, "F3"),
    F4(GLFW_KEY_F4, "F4"),
    F5(GLFW_KEY_F5, "F5"),
    F6(GLFW_KEY_F6, "F6"),
    F7(GLFW_KEY_F7, "F7"),
    F8(GLFW_KEY_F8, "F8"),
    F9(GLFW_KEY_F9, "F9"),
    F10(GLFW_KEY_F10, "F10"),
    F11(GLFW_KEY_F11, "F11"),
    F12(GLFW_KEY_F12, "F12"),
    PrintScreen(GLFW_KEY_PRINT_SCREEN, "Print Screen"),
    ScrollLock(GLFW_KEY_SCROLL_LOCK, "Scroll Lock"),
    Pause(GLFW_KEY_PAUSE, "Pause"),

    Grave(GLFW_KEY_GRAVE_ACCENT, "`"),
    Num1(GLFW_KEY_1, "1"),
    Num2(GLFW_KEY_2, "2"),
    Num3(GLFW_KEY_3, "3"),
    Num4(GLFW_KEY_4, "4"),
    Num5(GLFW_KEY_5, "5"),
    Num6(GLFW_KEY_6, "6"),
    Num7(GLFW_KEY_7, "7"),
    Num8(GLFW_KEY_8, "8"),
    Num9(GLFW_KEY_9, "9"),
    Num0(GLFW_KEY_0, "0"),
    Minus(GLFW_KEY_MINUS, "-"),
    Equals(GLFW_KEY_EQUAL, "="),
    Backspace(GLFW_KEY_BACKSPACE, "Backspace"),
    Insert(GLFW_KEY_INSERT, "Insert"),
    Home(GLFW_KEY_HOME, "Home"),
    PageUp(GLFW_KEY_PAGE_UP, "Page Up"),

    Tab(GLFW_KEY_TAB, "Tab"),
    Q(GLFW_KEY_Q, "Q"),
    W(GLFW_KEY_W, "W"),
    E(GLFW_KEY_E, "E"),
    R(GLFW_KEY_R, "R"),
    T(GLFW_KEY_T, "T"),
    Y(GLFW_KEY_Y, "Y"),
    U(GLFW_KEY_U, "U"),
    I(GLFW_KEY_I, "I"),
    O(GLFW_KEY_O, "O"),
    P(GLFW_KEY_P, "P"),
    LeftBracket(GLFW_KEY_LEFT_BRACKET, "["),
    RightBracket(GLFW_KEY_RIGHT_BRACKET, "]"),
    Backslash(GLFW_KEY_BACKSLASH, "\\"),
    Delete(GLFW_KEY_DELETE, "Delete"),
    End(GLFW_KEY_END, "End"),
    PageDown(GLFW_KEY_PAGE_DOWN, "Page Down"),

    CapsLock(GLFW_KEY_CAPS_LOCK, "Caps Lock"),
    A(GLFW_KEY_A, "A"),
    S(GLFW_KEY_S, "S"),
    D(GLFW_KEY_D, "D"),
    F(GLFW_KEY_F, "F"),
    G(GLFW_KEY_G, "G"),
    H(GLFW_KEY_H, "H"),
    J(GLFW_KEY_J, "J"),
    K(GLFW_KEY_K, "K"),
    L(GLFW_KEY_L, "L"),
    SemiColon(GLFW_KEY_SEMICOLON, ";"),
    Apostrophe(GLFW_KEY_APOSTROPHE, "'"),
    Enter(GLFW_KEY_ENTER, "Enter"),

    LeftShift(GLFW_KEY_LEFT_SHIFT, "Left Shift"),
    Z(GLFW_KEY_Z, "Z"),
    X(GLFW_KEY_X, "X"),
    C(GLFW_KEY_C, "C"),
    V(GLFW_KEY_V, "V"),
    B(GLFW_KEY_B, "B"),
    N(GLFW_KEY_N, "N"),
    M(GLFW_KEY_M, "M"),
    Comma(GLFW_KEY_COMMA, ","),
    Period(GLFW_KEY_PERIOD, "."),
    ForwardSlash(GLFW_KEY_SLASH, "/"),
    RightShift(GLFW_KEY_RIGHT_SHIFT, "Right Shift"),

    LeftControl(GLFW_KEY_LEFT_CONTROL, "Left Control"),
    Menu(GLFW_KEY_MENU, "Menu"),
    LeftAlt(GLFW_KEY_LEFT_ALT, "Left Alt"),
    Space(GLFW_KEY_SPACE, "Space"),
    RightAlt(GLFW_KEY_RIGHT_ALT, "Right Alt"),
    RightControl(GLFW_KEY_RIGHT_CONTROL, "Right Control"),

    Up(GLFW_KEY_UP, "Up"),
    Down(GLFW_KEY_DOWN, "Down"),
    Left(GLFW_KEY_LEFT, "Left"),
    Right(GLFW_KEY_RIGHT, "Right");

    private final int code;
    private final String name;

    Keys(int code, String name)
    {
        this.code = code;
        this.name = name;
    }

    int getCode()
    {
        return this.code;
    }

    public String getName()
    {
        return this.name;
    }
}
