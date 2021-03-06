<template>
  <div class="mentions-sidebar">
    <b-sidebar
      :visible="toggle"
      id="mentions-sidebar"
      title="Stock mentions"
      right
      shadow
    >
      <div class="px-3 py-2">
        <p>
          Cras mattis consectetur purus sit amet fermentum. Cras justo odio, dapibus ac facilisis
          in, egestas eget quam. Morbi leo risus, porta ac consectetur ac, vestibulum at eros.
        </p>
      </div>
    </b-sidebar>
  </div>
</template>

<script>
import { BSidebar } from 'bootstrap-vue'

export default {
  name: 'MentionsSidebar',
  components: { BSidebar },
  props: {
    toggle: {
      type: Boolean,
      required: true
    }
  }
}
</script>

<style scoped lang="scss">

</style>
