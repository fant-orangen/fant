<script setup lang="ts">
import NavBar from '@/components/navbar/NavBar.vue'
</script>
 <template>
   <div class="container">
     <NavBar />
     <main class="main">
       <RouterView />
     </main>
   </div>
 </template>
<style scoped>
.container {
  padding: 2rem 1rem;
}
</style>
