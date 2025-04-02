export interface ItemPreviewType {
  id: string | number;
  title: string;
  imageUrl: string;
  price: number;
  categoryId: string;
}

export interface ItemDetailsType {
  id: string | number;
  title: string;
  description: string;
  category: string;
  price: number;
  location: string;
  contact: string;
  imageUrls: string[];
}
