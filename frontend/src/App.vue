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
  display: flex;
  flex-direction: column;
  min-height: 100vh;
  padding: 0;
  box-sizing: border-box;
}
.main {
  flex-grow: 1;
  margin: 0;
  padding: 1rem;
  box-sizing: border-box;
}
:deep(.nav) {
  flex-shrink: 0;
}
</style>
