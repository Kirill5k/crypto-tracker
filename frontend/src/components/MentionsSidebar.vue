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
        <div v-for="mention in mentions" :key="mention.time" class="mb-1">
          <hr class="border border-primary"/>
          <p class="mb-0">{{ mention.message }}</p>
          <a :href="mention.url" target="_blank" class="badge badge-primary text-wrap">
            /r/{{ mention.source.subreddit }}
          </a>
        </div>
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
    },
    mentions: {
      type: Array,
      required: true
    }
  }
}
</script>

<style scoped lang="scss">

</style>
