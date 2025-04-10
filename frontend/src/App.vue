<script setup lang="ts">
/**
 * Main App component.
 *
 * This is the root component of the application that provides the overall layout structure.
 * It includes the navigation bar and a router view container for displaying page content.
 *
 * Features:
 * - Consistent layout across all views
 * - Fixed navigation with responsive design
 * - Flexible content area that grows to fill available space
 *
 * @component
 */
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
