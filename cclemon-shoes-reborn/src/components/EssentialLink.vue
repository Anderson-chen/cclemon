<template>
  <q-item clickable tag="a" :to="to">
    <q-item-section v-if="icon" avatar>
      <q-icon :name="icon" />
    </q-item-section>

    <q-item-section>
      <q-item-label>{{ title }}</q-item-label>
      <q-item-label caption>{{ caption }}</q-item-label>
    </q-item-section>
  </q-item>
</template>

<script setup lang="ts">
defineOptions({
  name: 'EssentialLink',
});

export interface EssentialLinkProps {
  title: string;
  caption?: string;
  icon?: string;
  to?: string;
}

withDefaults(defineProps<EssentialLinkProps>(), {
  caption: '',
  icon: '',
  to: '',
});
</script>
