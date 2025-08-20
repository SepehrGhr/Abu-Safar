import type { Company } from './types';

const API_BASE_URL = 'http://localhost:8888/api';

export const getCompaniesByVehicleType = async (vehicleType: string): Promise<Company[]> => {
  if (!vehicleType.trim()) {
    return [];
  }

  const response = await fetch(`${API_BASE_URL}/companies/${vehicleType}`);

  if (!response.ok) {
    throw new Error('Failed to fetch companies');
  }

  const result = await response.json();
  return result.data || [];
};