export interface ItemPreviewType {
  id: string | number;
  title: string;
  imageUrl: string;
  price: number;
  categoryId: string;
  latitude?: number;
  longitude?: number;
}

export interface ItemDetailsType {
  id: string | number;
  title: string;
  description: string;
  category: string;
  price: number;
  // location: string;
  contact: string;
  imageUrls: string[];
  latitude?: number;
  longitude?: number;
  sellerId: number | string;
}

export interface ItemFavoritesType {
  itemId: string | number;
  createdAt: string;
}

export interface CreateItemType {
  categoryId: number;
  briefDescription: string;
  fullDescription: string;
  price: number;
  latitude?: number;
  longitude?: number;
  images: null; // explicitly null when creating
}

export interface ItemImageType {
  imageUrl: string;
  position: number;
}
