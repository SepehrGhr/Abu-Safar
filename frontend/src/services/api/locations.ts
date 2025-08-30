import type { Location } from './types';

const API_BASE_URL = 'http://localhost:8888/api';


export const searchCitiesByName = async (name: string): Promise<Location[]> => {
  if (!name.trim()) {
    return [];
  }

  const response = await fetch(`${API_BASE_URL}/locations/cities?name=${encodeURIComponent(name)}`);

  if (!response.ok) {
    throw new Error('Failed to search for locations');
  }

  const result = await response.json();

  return result.data || [];
};