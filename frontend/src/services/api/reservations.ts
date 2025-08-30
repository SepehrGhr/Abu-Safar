import  apiClient  from './apiClient';
import type { ReserveConfirmation, Ticket } from './types';

interface TicketSelectRequest {
  tripId: number;
  ageCategory: string;
}

export const createReservation = async (
  selectedTickets: Ticket[],
  isRoundTrip: boolean
): Promise<ReserveConfirmation> => {
  try {
    const ticketRequests: TicketSelectRequest[] = selectedTickets.map(ticket => ({
      tripId: parseInt(ticket.tripId, 10),
      ageCategory: ticket.ageCategory,
    }));

    let response;
    if (isRoundTrip) {
      response = await apiClient.post('/booking/reserve/two_way', ticketRequests);
    } else {
      response = await apiClient.post('/booking/reserve/one_way', ticketRequests[0]);
    }

    return response.data.data;

  } catch (error: any) {
    throw new Error(error.response?.data?.message || 'Failed to create reservation.');
  }
};