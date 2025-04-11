/**
 * Validation utilities module.
 *
 * This module provides validation functions and regular expressions for
 * validating common input types like passwords and phone numbers.
 *
 * @module validation
 */

/**
 * Regular expression for password validation.
 *
 * Ensures passwords contain:
 * - At least 8 characters
 * - At least one lowercase letter
 * - At least one uppercase letter
 * - At least one digit
 */
export const passwordRegex = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)[A-Za-z\d]{8,}$/;

/**
 * Validates a password against security requirements.
 *
 * Checks that the password exists and matches the complexity requirements
 * defined in the password regex (8+ chars, lowercase, uppercase, digit).
 *
 * @param {string} password - The password to validate
 * @returns {{ isValid: boolean, messageKey: string | null }}  Object containing validity status and message key
 */
export function validatePassword(password: string): { isValid: boolean; messageKey: string | null } {
  if (!password) {
    return { isValid: false, messageKey: 'PASSWORD_REQUIRED' };
  }
  if (!passwordRegex.test(password)) {
    return { isValid: false, messageKey: 'PASSWORD_COMPLEXITY' };
  }
  return { isValid: true, messageKey: null };
}

/**
 * Regular expression for international phone number validation.
 *
 * Ensures phone numbers:
 * - Start with a plus sign (+)
 * - Are followed by 6-14 digits
 * - May have single spaces between digits
 */
export const phoneNumberRegex = /^\+(?:[0-9] ?){6,14}[0-9]$/;

/**
 * Validates a phone number against international format requirements.
 *
 * Empty values are treated as valid since phone numbers are optional.
 * Non-empty values must match the international format with a plus sign
 * and appropriate number of digits.
 *
 * @param {string} phone - The phone number to validate
 * @returns {{ isValid: boolean, messageKey: string | null }}  Object containing validity status and message key
 */
export function validatePhoneNumber(phone: string): { isValid: boolean; messageKey: string | null } {
  if (!phone) {
    return { isValid: true, messageKey: null };
  }
  if (!phoneNumberRegex.test(phone)) {
    return { isValid: false, messageKey: 'PHONE_NUMBER_FORMAT_INVALID' };
  }
  return { isValid: true, messageKey: null };
}
