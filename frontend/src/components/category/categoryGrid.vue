<script setup lang="ts">
import CategoryButton from "@/components/category/categoryButton.vue";
import torgIcon from '@/assets/icons/torgIcon.svg'
import travelIcon from '@/assets/icons/travelIcon.svg'
import applianceIcon from '@/assets/icons/applianceIcon.svg'
import boatIcon from '@/assets/icons/boatIcon.svg'
import bookIcon from '@/assets/icons/bookIcon.svg'
import cameraIcon from '@/assets/icons/cameraIcon.svg'
import carIcon from '@/assets/icons/carIcon.svg'
import clothesIcon from '@/assets/icons/clothesIcon.svg'
import computerIcon from '@/assets/icons/computerIcon.svg'
import furnitureIcon from '@/assets/icons/furnitureIcon.svg'
import motorcycleIcon from '@/assets/icons/motorcycleIcon.svg'
import phoneIcon from '@/assets/icons/phoneIcon.svg'
import artIcon from '@/assets/icons/artIcon.svg'



const categories = [
  { label: "Torg", icon: torgIcon },
  { label: "Reise", icon: travelIcon },
  { label: "Hvitevarer", icon: applianceIcon },
  { label: "Båt", icon: boatIcon },
  { label: "Bok", icon: bookIcon },
  { label: "Kamera", icon: cameraIcon },
  { label: "Bil", icon: carIcon },
  { label: "Klær", icon: clothesIcon },
  { label: "Datamaskin", icon: computerIcon },
  { label: "Møbel", icon: furnitureIcon },
  { label: "Motorsykkel", icon: motorcycleIcon },
  { label: "Mobiltelefon", icon: phoneIcon },
  { label: "Kunst", icon: artIcon },
  { label: "Reise", icon: travelIcon }

]

const emit = defineEmits<(e: 'select', categoryLabel: string) => void>()

function handleFunction(label: string) {
  emit('select',label)
}
</script>

<template>
  <div class="category-grid">
    <CategoryButton
    v-for="category in categories"
    :key="category.label"
    :label="category.label"
    :icon="category.icon"
    @click="handleFunction(category.label)"/>
  </div>

</template>
