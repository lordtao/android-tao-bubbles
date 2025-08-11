package ua.at.tsvetkov.bubbles

/**
 * Created by Alexandr Tsvetkov on 28.07.2025.
 */

/**
 * Specifies the side of a bubble where the arrow pointer should be displayed.
 */
enum class ArrowPosition {
    /** The arrow is on the left side of the target component, pointing right. */
    LEFT,
    /** The arrow is on the right side of the target component, pointing left. */
    RIGHT,
    /** The arrow is on the top side of the target component, pointing downwards. */
    TOP,
    /** The arrow is on the bottom side of the target component, pointing upwards. */
    BOTTOM
}