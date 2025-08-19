import type { Ticket, TicketSearchRequest } from './types';

const API_BASE_URL = 'http://localhost:8888/api';

export const searchTickets = async (params: TicketSearchRequest): Promise<Ticket[]> => {
  const response = await fetch(`${API_BASE_URL}/tickets/search`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify(params),
  });

  if (!response.ok) {
    try {
        const errorData = await response.json();
        throw new Error(errorData.message || `Request failed with status ${response.status}`);
    } catch (e) {
            // This catch is triggered if the response body isn't valid JSON.
        throw new Error(`Request failed with status ${response.status}`);
    }
  }

  const result = await response.json();
  return result.data || [];
};
