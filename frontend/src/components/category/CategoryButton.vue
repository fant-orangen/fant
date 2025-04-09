<script setup lang="ts">
/**
 * CategoryButton.vue
 *
 * A versatile button component for displaying category options with icon and label.
 * Supports both compact (horizontal) and standard (vertical) layouts, as well as
 * visual indication of active/selected state.
 */

// Define component props with TypeScript interface
defineProps<{
  /**
   * Text label to display for the category
   */
  label: string;

  /**
   * URL or path to the category icon image
   */
  icon: string;

  /**
   * When true, displays in horizontal layout mode (icon left of text)
   * When false/undefined, displays in vertical layout mode (icon above text)
   * @default false
   */
  compact?: boolean;

  /**
   * When true, visually indicates this category is selected/active
   * @default false
   */
  active?: boolean;
}>();

// Note: This component emits standard DOM events like 'click'
// which should be handled by the parent component
</script>

<template>
  <!--
    Main button element with dynamic classes based on props:
    - Always has 'category-button' class
    - Has 'compact' class when compact prop is true
    - Has 'active' class when active prop is true
  -->
  <button :class="['category-button', { 'compact': compact, 'active': active }]">
    <!-- Icon image with proper alt text for accessibility -->
    <img :src="icon" :alt="label" class="category-icon" />

    <!-- Text label for the category -->
    <span class="category-label">{{ label }}</span>
  </button>
</template>

<style scoped>
/**
 * Base button styling - Standard vertical layout (non-compact mode)
 */
.category-button {
  /* Flex layout for positioning icon and label */
  display: flex;
  flex-direction: column; /* Vertical arrangement: icon above label */
  align-items: center;
  justify-content: center;

  /* Visual styling */
  background-color: white;
  border: 1px solid #ddd;
  border-radius: 8px;
  padding: 1.5rem;

  /* Interaction styling */
  cursor: pointer;
  transition: all 0.2s ease; /* Smooth transition for hover effects */
}

/**
 * Hover state styling
 */
.category-button:hover {
  background-color: #f0f0f0;
  transform: translateY(-2px); /* Subtle lift effect on hover */
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.1);
}

/**
 * Active (selected) state styling
 */
.category-button.active {
  background-color: #e0f2fe; /* Light blue background */
  border-color: #93c5fd; /* Blue border for emphasis */
}

/**
 * Category icon styling in standard mode
 */
.category-icon {
  width: 32px;
  height: 32px;
  margin-bottom: 0.5rem; /* Space between icon and label */
}

/**
 * Category label styling
 */
.category-label {
  font-size: 0.9rem;
  font-weight: 500;
  text-align: center;
}

/**
 * Compact mode button styling (horizontal layout)
 */
.category-button.compact {
  flex-direction: row; /* Horizontal arrangement: icon left of label */
  justify-content: flex-start;
  padding: 0.5rem 0.75rem; /* Reduced padding for compact display */
  width: 100%; /* Full width of container */
}

/**
 * Compact mode icon styling
 */
.category-button.compact .category-icon {
  width: 24px; /* Smaller icon in compact mode */
  height: 24px;
  margin-bottom: 0; /* Remove bottom margin from vertical layout */
  margin-right: 0.5rem; /* Add right margin to separate from label */
}
</style>
