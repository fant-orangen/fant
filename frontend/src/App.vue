<script setup lang="ts">
import NavBar from '@/components/navbar/navBar.vue'
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
  max-width: 1200px;
  margin: 0 auto;
  padding: 2rem 1rem;
}
</style>
