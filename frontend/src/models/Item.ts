export interface ItemPreviewType {
  id: string | number;
  title: string;
  imageUrl: string;
  price: number;
}

export interface ItemDetailsType {
  id: string | number;
  title: string;
  description: string;
  price: number;
  location: string;
  contact: string;
  imageUrls: string[];
}
