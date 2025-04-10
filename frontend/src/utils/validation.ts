export const passwordRegex = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)[A-Za-z\d]{8,}$/;

export function validatePassword(password: string): { isValid: boolean; messageKey: string | null } {
  if (!password) {
    // Return the key for the required message
    return { isValid: false, messageKey: 'PASSWORD_REQUIRED' };
  }
  if (!passwordRegex.test(password)) {
    // Return the key for the complexity message
    return { isValid: false, messageKey: 'PASSWORD_COMPLEXITY' };
  }
  // Return null when valid (no message needed)
  return { isValid: true, messageKey: null };
}

export const phoneNumberRegex = /^\+(?:[0-9] ?){6,14}[0-9]$/;

export function validatePhoneNumber(phone: string): { isValid: boolean; messageKey: string | null } {
  // Treat empty string as valid (optional field)
  if (!phone) {
    return { isValid: true, messageKey: null };
  }
  // Check format if not empty
  if (!phoneNumberRegex.test(phone)) {
    // Return key for invalid format message
    return { isValid: false, messageKey: 'PHONE_NUMBER_FORMAT_INVALID' };
  }
  // Valid format
  return { isValid: true, messageKey: null };
}
