import type { Ticket, TicketSearchRequest, TicketSelectRequest, TicketDetails } from './types';

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
        throw new Error(`Request failed with status ${response.status}`);
    }
  }

  const result = await response.json();
  return result.data || [];
};

export const selectTicket = async (params: TicketSelectRequest): Promise<TicketDetails> => {
  const response = await fetch(`${API_BASE_URL}/tickets/select`, {
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
        throw new Error(`Request failed with status ${response.status}`);
    }
  }

  const result = await response.json();
  return result.data;
};