<script setup lang="ts">
import { useI18n } from 'vue-i18n'

import CategoryButton from "@/components/category/categoryButton.vue";
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

const { t } = useI18n()


const categories = [

  { labelKey: "category.appliance", icon: applianceIcon },
  { labelKey: "category.boat", icon: boatIcon },
  { labelKey: "category.book", icon: bookIcon },
  { labelKey: "category.cameras", icon: cameraIcon },
  { labelKey: "category.cars", icon: carIcon },
  { labelKey: "category.clothes", icon: clothesIcon },
  { labelKey: "category.computers", icon: computerIcon },
  { labelKey: "category.furniture", icon: furnitureIcon },
  { labelKey: "category.motorcycle", icon: motorcycleIcon },
  { labelKey: "category.phone", icon: phoneIcon },
  { labelKey: "category.art", icon: artIcon },
  { labelKey: "category.travel", icon: travelIcon }

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
    :key="category.labelKey"
    :label="t(category.labelKey)"
    :icon="category.icon"
    @click="handleFunction(category.labelKey)"/>
  </div>

</template>
