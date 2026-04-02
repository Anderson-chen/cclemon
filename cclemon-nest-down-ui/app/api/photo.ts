import type { BodyPhoto } from '@/types/models'

export const photoApi = {
  async getPhotos(): Promise<BodyPhoto[]> {
    return []
  },

  async uploadPhoto(file: File, date: string, angle?: BodyPhoto['angle']): Promise<BodyPhoto> {
    const url = URL.createObjectURL(file)
    return {
      id: Date.now().toString(),
      userId: '1',
      date,
      url,
      thumbnailUrl: url,
      angle
    }
  },

  async deletePhoto(id: string): Promise<void> {},
}
