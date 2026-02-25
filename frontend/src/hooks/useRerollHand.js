import { useState } from "react";

export const useRerollHand = (playerGameSessionId, jwt, onSuccess) => {
  const [isRerolling, setIsRerolling] = useState(false);
  const [hasRerolled, setHasRerolled] = useState(false);
  const [error, setError] = useState(null);

  const rerollHand = async () => {
    if (!playerGameSessionId || !jwt) return;

    if (hasRerolled || isRerolling) return;

    const confirmed = window.confirm(
        "Do you want to change your initial cards? You can only do this once."
    );
    if (!confirmed) return;

    setIsRerolling(true);
    setError(null);

    try {
      const response = await fetch(`/api/v1/cards/${playerGameSessionId}/reroll-hand`, {
        method: "PUT",
        headers: {
          Authorization: `Bearer ${jwt}`,
          "Content-Type": "application/json",
        },
      });

      if (!response.ok) {
        const message = await response.text();
        throw new Error(message || "Error rerolling hand");
      }

      setHasRerolled(true);
      onSuccess?.();
    } catch (err) {
      setError(err.message);
    } finally {
      setIsRerolling(false);
    }
  };

  return { rerollHand, hasRerolled, isRerolling, error };
};
