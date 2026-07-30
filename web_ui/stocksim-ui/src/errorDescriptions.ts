// Error descriptions mapping from /docs/problems
export const errorDescriptions: Record<string, string> = {
  "email-already-in-use":
    "There is already a Participant with given email address.",
};

export function getErrorDescription(errorType: string): string {
  return errorDescriptions[errorType] || errorType;
}
