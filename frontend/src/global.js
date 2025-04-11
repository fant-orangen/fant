/**
 * Global window polyfill.
 *
 * This file provides a polyfill that assigns the window object to window.global.
 * It's needed to fix compatibility issues between browser environments and
 * certain libraries that expect the global object to be available.
 *
 * This polyfill is particularly useful when working with libraries that were
 * originally designed for Node.js environments where 'global' is available,
 * but need to run in browser environments where 'window' is the global object.
 */
window.global = window;
